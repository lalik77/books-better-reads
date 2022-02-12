package com.mami.betterreads.user;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BooksByUserRepository extends CassandraRepository<BooksByUser,String> {

  Slice<BooksByUser> findAllById(String id, Pageable pageable);
  void deleteByIdAndBookId(String user_id,String book_id);

}
