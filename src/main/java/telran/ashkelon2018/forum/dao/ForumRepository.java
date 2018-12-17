package telran.ashkelon2018.forum.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {
	
	Stream<Post> findAllBy();

	Iterable<Post> findByTagsIn(List<String> tags);

	Iterable<Post> findByAuthor(String author);

	Iterable<Post> findPostByDateCreatedBetween(LocalDateTime from, LocalDateTime to);
	
	

}
