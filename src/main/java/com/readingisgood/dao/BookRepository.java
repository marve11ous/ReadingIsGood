package com.readingisgood.dao;

import com.readingisgood.dao.dto.Book;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select B from Book B where B.id in (:ids)")
    Iterable<Book> getAllForUpdateById(Iterable<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> getForUpdateById(Long id);
}
