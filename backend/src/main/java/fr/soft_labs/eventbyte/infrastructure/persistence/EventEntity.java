package fr.soft_labs.eventbyte.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String cover;

    @Column(name = "start_at", columnDefinition = "timestamptz")
    private Instant startAt;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @Column(name = "venue_address", nullable = false)
    private String venueAddress;

    @Column(name = "venue_latitude", nullable = false, columnDefinition = "float8")
    private double venueLatitude;

    @Column(name = "venue_longitude", nullable = false, columnDefinition = "float8")
    private double venueLongitude;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> tickets = new ArrayList<>();

    protected EventEntity() {
    }

    public EventEntity(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getVenueAddress() { return venueAddress; }
    public void setVenueAddress(String venueAddress) { this.venueAddress = venueAddress; }
    public double getVenueLatitude() { return venueLatitude; }
    public void setVenueLatitude(double venueLatitude) { this.venueLatitude = venueLatitude; }
    public double getVenueLongitude() { return venueLongitude; }
    public void setVenueLongitude(double venueLongitude) { this.venueLongitude = venueLongitude; }
    public List<TicketEntity> getTickets() { return tickets; }
}
