package org.example.centralserver.repo;


import org.example.centralserver.Entity.Transection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransectionRepo extends MongoRepository<Transection, String> {
}
