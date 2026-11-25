package fr.soft_labs.eventbyte.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataBookingRepository extends JpaRepository<BookingEntity, String> {
}
