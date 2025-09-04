package com.goorm.derere.repository;

import com.goorm.derere.entity.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantTypeRepository extends JpaRepository<RestaurantType, Long> {

    Optional<RestaurantType> findByTypeName(RestaurantType.TypeName typeName);
}