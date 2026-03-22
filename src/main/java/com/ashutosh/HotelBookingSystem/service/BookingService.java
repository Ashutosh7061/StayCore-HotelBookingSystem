package com.ashutosh.HotelBookingSystem.service;

import com.ashutosh.HotelBookingSystem.Enum.*;
import com.ashutosh.HotelBookingSystem.dto.*;
import com.ashutosh.HotelBookingSystem.entity.*;
import com.ashutosh.HotelBookingSystem.exception.*;
import com.ashutosh.HotelBookingSystem.repository.*;
import com.ashutosh.HotelBookingSystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ashutosh.HotelBookingSystem.Mapper.helperFunctions;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final HotelService hotelService;
    private final CancellationTransactionRepository cancellationTransactionRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public BookingResponseDTO bookRoom(UserOnlineBookingRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();


        Long userId = loggedUser.getReferenceId();

        LocalDate checkInDate = request.getCheckInDate();
        LocalDate checkOutDate = request.getCheckOutDate();

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        //check hotel status
        if(hotel.getStatus() != HotelStatus.APPROVED){
            throw new UnauthorizedAccessException("Hotel temporarily unavailable for new bookings");
        }

        // Duplicate booking check
        List<Booking> existingBookings =
                bookingRepository
                        .findByUser_IdAndHotel_IdAndRoomTypeAndCheckInDateAndCheckOutDateAndStatus(
                                userId,
                                request.getHotelId(),
                                request.getRoomType(),
                                checkInDate,
                                checkOutDate,
                                BookingStatus.CONFIRMED
                        );

        if (!existingBookings.isEmpty() && (request.getDuplicateBooking() == null || !request.getDuplicateBooking())) {
            throw new DuplicateDataException(
                    "Same booking already exists. If you want to book again, pass duplicateBooking=true."
            );
        }

        if(checkOutDate.isBefore(checkInDate)
                || checkOutDate.isEqual(checkInDate)){
            throw new InvalidCheckOutDateException("Invalid check-out date, please provide correct check-out date.");
        }
        if(checkInDate.isBefore(LocalDate.now())){
            throw new InvalidCheckOutDateException("Check-in date must be valid");
        }

        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new DataNotFoundException("User not found."));


        List<Room> availableRooms = roomRepository
                        .findByHotel_IdAndRoomTypeAndStatus(request.getHotelId(), request.getRoomType(), RoomStatus.VACANT);

        if(availableRooms.size() < request.getNoOfRooms()){
            throw new DataNotFoundException("Not enough rooms available, there are only "+ availableRooms.size()+" rooms available");
        }

        //Price calculation
        double pricePerRoom = availableRooms.get(0).getPrice();
        double totalPrice = pricePerRoom * days * request.getNoOfRooms();


        List<Room> roomsToBook = availableRooms.stream()
                .limit(request.getNoOfRooms())
                .toList();

        for(Room room : roomsToBook){
            room.setStatus(RoomStatus.BOOKED);
        }

        roomRepository.saveAll(roomsToBook);

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setBookingReferenceId(generateBookingReferenceId());
        booking.setHotel(hotel);
        booking.setRoomType(request.getRoomType());
        booking.setNumberOfRooms(request.getNoOfRooms());
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setCheckInInstruction(request.getCheckInInstruction());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        booking.setIsWalkIn(false);
        booking.setBookingSource(BookingSource.ONLINE);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponseDTO(
                savedBooking.getId(),
                savedBooking.getBookingReferenceId(),
                hotel.getId(),
                hotel.getHotelName(),
                (int)days,
                request.getNoOfRooms(),
                savedBooking.getCheckInInstruction(),
                savedBooking.getCheckInDate(),
                savedBooking.getCheckOutDate(),
                totalPrice,
                savedBooking.getStatus().name(),
                savedBooking.getBookingTime()
        );

    }

    public List<UserBookingResponseDTO> getIndividualUserBookings(BookingStatus status){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        List<Booking> bookings;

        if (status == null) {
            bookings = bookingRepository.findBookingsWithHotel(userId);
        } else {
            bookings = bookingRepository.findBookingsWithHotelAndStatus(userId, status);
        }

        if (bookings.isEmpty()) {
            throw new DataNotFoundException("No bookings found for user id: " + userId);
        }
        return bookings.stream()
                .map(booking -> {
                    int days = (int) ChronoUnit.DAYS.between(
                            booking.getCheckInDate(),
                            booking.getCheckOutDate()
                    );
                    AddressDTO addressDTO = new AddressDTO(
                            booking.getHotel().getAddressLine(),
                            booking.getHotel().getCity(),
                            booking.getHotel().getState(),
                            booking.getHotel().getPinCode()
                    );

                    return new UserBookingResponseDTO(
                            booking.getId(),
                            booking.getBookingReferenceId(),
                            booking.getHotel().getHotelName(),
                            addressDTO,
                            days,
                            booking.getNumberOfRooms(),
                            booking.getStatus().name(),
                            booking.getBookingTime()
                    );
                })
                .toList();
    }


    public List<HotelBookingSummaryDTO> getHotelBookings() {

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        List<Booking> bookings = bookingRepository.findByHotelId(hotelId);

        if(bookings.isEmpty()){
            throw new DataNotFoundException("No booking found for the hotel with id: "+ hotelId);
        }

        return bookings.stream()
                .map(booking -> {

                    int days = (int) ChronoUnit.DAYS.between(
                            booking.getCheckInDate(),
                            booking.getCheckOutDate()
                    );

                    String name = getCustomerName(booking);
                    String phoneNo = getCustomerPhone(booking);

                    return new HotelBookingSummaryDTO(
                            booking.getId(),
                            booking.getBookingReferenceId(),
                            booking.getBookingSource(),
                            name,
                            phoneNo,
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getRoomType(),
                            booking.getNumberOfRooms(),
                            booking.getStatus().name(),
                            booking.getBookingTime()
                    );
                })
                .toList();
    }

    public HotelSpecificBookingDetailsDTO getIndividualBookingDetails(Long bookingId){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new DataNotFoundException("Booking not found with given id"));

        if(!booking.getHotel().getId().equals(hotelId)){
            throw new UnauthorizedAccessException("This booking does not exist in your hotel.");
        }

        HotelSpecificBookingDetailsDTO response = new HotelSpecificBookingDetailsDTO();

        response.setBookingId(booking.getId());
        response.setUserName(getCustomerName(booking));
        response.setUserPhoneNo(getCustomerPhone(booking));
        response.setUserEmail(getCustomerEmail(booking));

        response.setBookingSource(booking.getBookingSource());

        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());

        response.setRoomType(booking.getRoomType());
        response.setNoOfRooms(booking.getNumberOfRooms());
        response.setNoOfDays(booking.getNumberOfRooms());
        response.setAllottedRoomNumber(booking.getAllottedRoomNumber());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setBookingTime(booking.getBookingTime());

        return response;
    }


    public BookingSummaryDTO getBookingSummary(Long bookingId){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new DataNotFoundException("Booking not found for this bookingId."));


        if(booking.getUser() == null || !booking.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("This booking does not exist in your hotel.");
        }

        int days = helperFunctions.calculateDays(booking);

        String fullAddress = booking.getHotel().getAddressLine()+ ", "+
                booking.getHotel().getCity()+", "+ booking.getHotel().getState()+", "+
                booking.getHotel().getPinCode();

        return new BookingSummaryDTO(
                booking.getId(),
                booking.getBookingReferenceId(),

                booking.getHotel().getId(),
                booking.getHotel().getHotelName(),
                fullAddress,
                booking.getAllottedRoomNumber(),
                days,
                booking.getNumberOfRooms(),
                booking.getTotalPrice(),
                booking.getStatus().name(),
                booking.getBookingTime()
        );
    }


    public List<BaseUserPerHotelResponseDTO> getAllUserOfHotelByStatus(Long hotelId, BookingStatus status){

        hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new DataNotFoundException("Hotel not found with id: " + hotelId)
                );

        List<Booking> bookings;

        if(status == null){
            bookings = bookingRepository.findByHotel_Id(hotelId);
        }
        else{
            bookings = bookingRepository.findBookingsByHotelAndStatus(hotelId,status);
        }

        if (bookings.isEmpty()) {
            throw new DataNotFoundException("No booking found for status " + status);
        }

        return bookings.stream()
                .map(booking -> {

                    String name = getCustomerName(booking);
                    String phone = getCustomerPhone(booking);
                    String email = getCustomerEmail(booking);

                    Long userId = booking.getUser() != null ? booking.getUser().getId() : null;

                    LocalDateTime createdAt = booking.getUser() != null
                            ? booking.getUser().getCreatedAt()
                            : booking.getBookingTime();

                    String customerType = Boolean.TRUE.equals(booking.getIsWalkIn())
                            ? "GUEST"
                            : "USER";

                    if(status == null){
                        return new ConfirmedUserPerHotelResponseDTO(
                          userId,
                          customerType,
                          name,
                          email,
                          phone,
                          createdAt,
                          booking.getCheckInDate(),
                          booking.getCheckOutDate(),
                          booking.getNumberOfRooms()
                        );
                    }

                    switch (booking.getStatus()){
                        case CANCELLED:
                            CancellationTransaction tx = cancellationTransactionRepository.findByBookingId(booking.getId())
                                    .orElseThrow(()-> new DataNotFoundException("Cancellation record not found for booking id: "+ booking.getId()));

                            return new CancelledUserPerHotelResponseDTO(
                                    userId,
                                    customerType,
                                    name,
                                    email,
                                    phone,
                                    createdAt,
                                    tx.getCancelledBy(),
                                    tx.getCancellationReason()
                            );
                        case COMPLETED:
                            Review review = reviewRepository.findByBooking_Id(booking.getId())
                                    .orElseThrow(()-> new DataNotFoundException("Review not found"));

                            return new CompletedUserPerHotelResponseDTO(
                                    userId,
                                    customerType,
                                    name,
                                    email,
                                    phone,
                                    createdAt,
                                    review.getComment(),
                                    review.getRating()
                            );
                        case CONFIRMED:
                            return new ConfirmedUserPerHotelResponseDTO(
                                    userId,
                                    customerType,
                                    name,
                                    email,
                                    phone,
                                    createdAt,
                                    booking.getCheckInDate(),
                                    booking.getCheckOutDate(),
                                    booking.getNumberOfRooms()
                            );

                        default:
                            return new BaseUserPerHotelResponseDTO(
                                    userId,
                                    customerType,
                                    name,
                                    email,
                                    phone,
                                    createdAt
                            );
                    }
                })
                .toList();
    }


    @Transactional
    public CancellationResponseDTO cancelBooking(Long bookingId, CancelledBy cancelledBy, String reason){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String role = loggedUser.getRole();
        Long referenceId = loggedUser.getReferenceId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new DataNotFoundException("Booking not found for this given id."));


        // Prevent duplicate cancellation
        if(cancellationTransactionRepository.findByBookingId(bookingId).isPresent()){
            throw new InvalidBookingStateException("Cancellation already processed for this booking");
        }

        if(role.equals("USER")){
            if(!booking.getUser().getId().equals(referenceId)){
                throw new UnauthorizedAccessException("You can cancel yours own bookings.");
            }
            if(cancelledBy != CancelledBy.USER){
                throw new BookingValidationException("Invalid cancellation type for user");
            }
        }
        else if(role.equals("HOTEL")){
            if(!booking.getHotel().getId().equals(referenceId)){
                throw new UnauthorizedAccessException("You can cancel only booking of your own hotel");
            }
            if(cancelledBy != CancelledBy.HOTEL){
                throw new BookingValidationException("Invalid cancellation type for hotel");
            }
        }
        if(booking.getStatus() == BookingStatus.CHECKED_IN){
            throw new BookingCancellationException("Checked-in booking cannot be cancelled");
        }

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new InvalidBookingStateException("Booking already cancelled");
        }

        if(booking.getStatus() == BookingStatus.COMPLETED){
            throw new InvalidBookingStateException("Completed booking cannot be cancelled");
        }

        double totalPrice= booking.getTotalPrice();
        double deduction = 0;
        double refund =totalPrice;
        double deductionPercentage = 0;

        CancellationTransaction tx = new CancellationTransaction();

        if(cancelledBy == CancelledBy.USER){

            LocalDate today = LocalDate.now();
            LocalDate checkInDate = booking.getCheckInDate();

            long daysBetween = ChronoUnit.DAYS.between(today, checkInDate);

            if(daysBetween < 0){
                throw new InvalidBookingStateException("Cannot cancel after check-in date");
            }

            if(daysBetween <= 0){
                deductionPercentage = 0.50;
            }
            else if(daysBetween <= 3){
                deductionPercentage = 0.15;
            }
            else{
                deductionPercentage = 0.0;
            }

            deduction = totalPrice * deductionPercentage;
            refund = totalPrice - deduction;

            tx.setCancellationReason(null);
        }

        if(cancelledBy == CancelledBy.HOTEL){

            hotelService.validateHotelOperation(booking.getHotel());

            if(reason == null || reason.isBlank()){
                throw new BookingValidationException("Cancellation reason requires when hotel cancels booking");
            }
            deduction = 0;
            refund = totalPrice;
            deductionPercentage = 0;

            tx.setCancellationReason(reason);
            //In future credits to be added
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Save cancellation transaction
        tx.setBookingId(booking.getId());
        tx.setBookingReferenceId(booking.getBookingReferenceId());
        tx.setOriginalAmount(totalPrice);
        tx.setDeductionAmount(deduction);
        tx.setRefundAmount(refund);
        tx.setDeductionPercentage(deductionPercentage);
        tx.setCancelledBy(cancelledBy);
        tx.setCancellationTime(LocalDateTime.now());

        cancellationTransactionRepository.save(tx);

        List<Room> rooms;

        // CASE 1 : Cancel before check-in
        if(booking.getAllottedRoomNumber() == null || booking.getAllottedRoomNumber().isEmpty()){
            rooms = roomRepository.findByHotel_IdAndRoomTypeAndStatus(
                    booking.getHotel().getId(),
                    booking.getRoomType(),
                    RoomStatus.BOOKED
            );
            if(rooms.size() < booking.getNumberOfRooms()){
                throw new IllegalStateException("Not enough booked rooms found to release");
            }
            for(int i = 0; i < booking.getNumberOfRooms(); i++){
                rooms.get(i).setStatus(RoomStatus.VACANT);
            }
        }

        // CASE 2 : Cancel after check-in
        else{
            List<String> roomNumbers = booking.getAllottedRoomNumber();
            rooms = roomRepository.findByHotel_IdAndRoomNumberIn(
                    booking.getHotel().getId(),
                    roomNumbers
            );
            for(Room room : rooms){
                room.setStatus(RoomStatus.VACANT);
            }
        }

        roomRepository.saveAll(rooms);

        return new CancellationResponseDTO(
                booking.getId(),
                cancelledBy.name(),
                reason,
                booking.getTotalPrice(),
                deduction,
                refund,
                booking.getStatus().name()
        );
    }

    public List<AdminBookingDetailsDTO> getCompletedBookingForAdmin(){

        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        return bookings.stream()
                .map(booking -> {

                    Optional<Review> reviewOpt = reviewRepository.findByBooking_Id(booking.getId());
                    Integer rating = null;
                    String comment = null;

                    if(reviewOpt.isPresent()){
                        rating = reviewOpt.get().getRating();
                        comment = reviewOpt.get().getComment();
                    }

                    Long userId = booking.getUser() != null ? booking.getUser().getId() : null;

                    String name;

                    if(Boolean.TRUE.equals(booking.getIsWalkIn())){
                        name = booking.getGuestName();
                    } else if(booking.getUser() != null){
                        name = booking.getUser().getName();
                    } else {
                        name = "Unknown"; // fallback safety
                    }


                    return new AdminBookingDetailsDTO(
                            booking.getId(),
                            userId,
                            name,
                            booking.getHotel().getId(),
                            booking.getHotel().getHotelName(),
                            booking.getTotalPrice(),
                            booking.getStatus(),
                            rating,
                            comment
                    );
                })
                .toList();
    }


    public  String generateBookingReferenceId(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        String bookingReferenceId;

        do{
            StringBuilder builder = new StringBuilder("HTL-");

            for(int i = 0; i < 6 ; i++){
                int index = random.nextInt(characters.length());
                builder.append(characters.charAt(index));
            }
            bookingReferenceId = builder.toString();
        }while (bookingRepository.findByBookingReferenceId(bookingReferenceId).isPresent());

        return bookingReferenceId;
    }


    public BookingResponseDTO offlineBooking(HotelOfflineBookingRequestDTO request){

        CustomUserDetails loggedUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long hotelId = loggedUser.getReferenceId();

        LocalDate checkInDate = request.getCheckInDate();
        LocalDate checkOutDate = request.getCheckOutDate();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new DataNotFoundException("Hotel not found"));

        //check hotel status
        if(hotel.getStatus() != HotelStatus.APPROVED){
            throw new UnauthorizedAccessException("Hotel temporarily unavailable for new bookings");
        }

        String idNumber = request.getGuestIdNumber();

        List<Booking> existingBookings =
                bookingRepository.findDuplicateByIdentity(
                        hotelId,
                        request.getRoomType(),
                        checkInDate,
                        checkOutDate,
                        idNumber
                );

        if (!existingBookings.isEmpty()) {
            throw new DuplicateDataException("Guest already has booking for same dates");
        }

        if(checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)){
            throw new InvalidCheckOutDateException("Invalid check-out date, please provide correct check-out date.");
        }
        if(checkInDate.isBefore(LocalDate.now())){
            throw new InvalidCheckOutDateException("Check-in date must be valid");
        }

        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);


        List<Room> availableRooms = roomRepository
                .findByHotel_IdAndRoomTypeAndStatus(hotelId, request.getRoomType(), RoomStatus.VACANT);

        if(availableRooms.size() < request.getNoOfRooms()){
            throw new DataNotFoundException("Only "+ availableRooms.size()+" rooms available");
        }

        //Price calculation
        double pricePerRoom = availableRooms.get(0).getPrice();
        double totalPrice = pricePerRoom * days * request.getNoOfRooms();

        List<Room> roomsToBook = availableRooms.stream()
                .limit(request.getNoOfRooms())
                .toList();

        for(Room room : roomsToBook){
            room.setStatus(RoomStatus.BOOKED);
        }

        roomRepository.saveAll(roomsToBook);

        Booking booking = new Booking();

        booking.setUser(null);

        booking.setGuestName(request.getGuestName());
        booking.setGuestPhoneNo(request.getGuestPhoneNo());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setGuestIdType(request.getGuestIdType());
        booking.setGuestIdNumber(request.getGuestIdNumber());
        booking.setGuestAddress(request.getGuestAddress());

        booking.setIsWalkIn(true);
        booking.setBookingSource(BookingSource.OFFLINE);

        booking.setBookingReferenceId(generateBookingReferenceId());
        booking.setHotel(hotel);
        booking.setRoomType(request.getRoomType());
        booking.setNumberOfRooms(request.getNoOfRooms());
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponseDTO(
                savedBooking.getId(),
                savedBooking.getBookingReferenceId(),
                hotel.getId(),
                hotel.getHotelName(),
                (int)days,
                request.getNoOfRooms(),
                savedBooking.getCheckInInstruction(),
                savedBooking.getCheckInDate(),
                savedBooking.getCheckOutDate(),
                totalPrice,
                savedBooking.getStatus().name(),
                savedBooking.getBookingTime()
        );

    }

    // For getting user/guest name and phoneNumber.
    private String getCustomerName(Booking booking){
        if(Boolean.TRUE.equals(booking.getIsWalkIn())){
            return booking.getGuestName();
        }
        return booking.getUser() != null ? booking.getUser().getName() : "Unknown";
    }

    private String getCustomerPhone(Booking booking){
        if(Boolean.TRUE.equals(booking.getIsWalkIn())){
            return booking.getGuestPhoneNo();
        }
        return booking.getUser() != null ? booking.getUser().getPhoneNo() : "N/A";
    }
    private String getCustomerEmail(Booking booking){
        if(Boolean.TRUE.equals(booking.getIsWalkIn())){
            return booking.getGuestEmail();
        }
        return booking.getUser() != null ? booking.getUser().getEmail() : null;
    }

}
