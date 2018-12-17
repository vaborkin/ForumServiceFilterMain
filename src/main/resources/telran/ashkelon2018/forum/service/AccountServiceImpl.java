package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exceptions.UserConflictException;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	UserAccountRepository userRepository;
	
	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		if (userRepository.existsById(credentials.getLogin())) {
			throw new UserConflictException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder()
				.login(credentials.getLogin())
//				.password(hashPassword.getPassword())
				.password(hashPassword)
				.firstName(userRegDto.getFirstName())
				.lastname(userRegDto.getLastName())
				.role("user")
				.expdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod())
				.build();
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}
	
	private UserProfileDto convertToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder()
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.login(userAccount.getLogin())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePassword(String password, String token) {
		// TODO Auto-generated method stub

	}

}
