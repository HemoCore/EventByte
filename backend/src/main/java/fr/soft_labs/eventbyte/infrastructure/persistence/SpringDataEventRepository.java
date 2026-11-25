package fr.soft_labs.eventbyte.infrastructure.persistence;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;


interface SpringDataEventRepository extends JpaRepository<EventEntity, String>{
}
