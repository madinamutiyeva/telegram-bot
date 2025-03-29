package com.cryptobot.repository;

import com.cryptobot.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SubscribersRepository extends JpaRepository<Subscriber, Integer> {

    Optional<Subscriber> findByChatId(Long chatId);

    void deleteByChatId(Long chatId);

    @Query("SELECT s FROM Subscriber s WHERE s.price >= :bitcoinPrice")
    List<Subscriber> findSubscribersWithPriceLessThan(@Param("bitcoinPrice") double bitcoinPrice);

}
