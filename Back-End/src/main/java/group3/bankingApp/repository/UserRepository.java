
package group3.bankingApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import group3.bankingApp.model.User;
import java.util.Optional; 
import java.util.List; 

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // You can add custom queries if needed
    // 

    // example of custom query
    //@Query("SELECT p FROM Publishers p WHERE p.journals > :minJournals AND p.location = :location")
    //List<Publisher> findPublishersWithMinJournalsInLocation(Integer minJournals,String location); 
    // source: https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa
    Optional<User> findByUsername(String username);

      Optional<User> findByEmail(String email);      

    Optional<User> findByBsn(String bsn);           

    List<User> findByUsernameContainingIgnoreCase(String username);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findAll(Pageable pageable);


}

