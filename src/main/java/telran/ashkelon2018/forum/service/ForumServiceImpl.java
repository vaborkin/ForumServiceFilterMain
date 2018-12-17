package telran.ashkelon2018.forum.service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.PermissionsRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exceptions.AcessForbiddenException;
import telran.ashkelon2018.forum.exceptions.PostNotFoundException;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Autowired
	PermissionsRepository permissionsRepository;

	@Autowired
	ForumRepository forumRepository;

	@Override
	public Post addNewPost(NewPostDto newPost, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		Post post = new Post(newPost.getTitle(), newPost.getContent(), credentials.getLogin(), newPost.getTags());
		forumRepository.save(post);
		return post;
	}

	@Override
	public Post getPost(String id) {
		return forumRepository.findById(id).orElseThrow(PostNotFoundException::new);
	}

	@Override
	public Post removePost(String id, String token) {
		String target = "removePost";
		boolean check = checkUserPermission(target, token);
		if (check) {
			Post post = forumRepository.findById(id).orElse(null);
			if (post != null) {
				forumRepository.deleteById(id);
			}
			return post;
		}
		throw new AcessForbiddenException();
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto, String token) {
		String target = "updatePost";
		boolean check = checkUserPermission(target, token);
		if (check) {
			Post post = forumRepository.findById(postUpdateDto.getId()).orElse(null);
			if (post == null) {
				return null;
			}
			post.setContent(postUpdateDto.getContent());
			post.setTitle(postUpdateDto.getTitle());
			post.setTags(postUpdateDto.getTags());
			forumRepository.save(post);
			return post;
		}
		throw new AcessForbiddenException();
	}

	@Override
	public boolean addLike(String id) {
		Post post = forumRepository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		post.addLike();
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		Post post = forumRepository.findById(id).orElse(null);
		if (post == null) {
			return null;
		}
		Comment comment = new Comment(credentials.getLogin(), newComment.getMessage());
		post.addComment(comment);
		return post;
	}

	@Override
	public Iterable<Post> findPostsByTags(List<String> tags) {
		return forumRepository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findPostsByAuthor(String author) {
		return forumRepository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datesDto) {
		return forumRepository.findPostByDateCreatedBetween(datesDto.getFrom(), datesDto.getTo());
	}

	private boolean checkUserPermission(String target, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		Set<String> userRoles = new TreeSet<>();
		userRoles.addAll(userAccount.getRoles());
		Set<String> permissions = new TreeSet<>();
		permissions.addAll(permissionsRepository.findById(target).get().getPermissions());
		Set<String> res = new TreeSet<String>(userRoles);
		res.retainAll(permissions);
		int result = res.size();
		if (result > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean checkUserPermission(String target, String token, String login) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		Set<String> permissions = new TreeSet<>();
		permissions.addAll(permissionsRepository.findById(target).get().getPermissions());
		boolean res1 = checkUserPermission(target, token);
		if (res1) {
			return true;
		} else {
			if (permissions.contains("Owner") && userAccount.getLogin().equals(login)) {
				return true;
			}
		}
		return false;
	}

}
