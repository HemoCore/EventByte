package fr.soft_labs.eventbyte.domain.ports.shared;

@FunctionalInterface 
public interface IdGenerator {
    String nextId();
}
