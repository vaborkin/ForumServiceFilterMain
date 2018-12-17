package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.PermissionsRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exceptions.AcessForbiddenException;
import telran.ashkelon2018.forum.exceptions.UserConflictException;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	UserAccountRepository userRepository;
	
	@Autowired
	AccountConfiguration accountConfiguration;

//	@Autowired
//	UserRolesRepository userRolesRepository;
//	
	@Autowired
	PermissionsRepository permissionsRepository;
	
	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		if(userRepository.existsById(credentials.getLogin())) {
			throw new UserConflictException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder()
				.login(credentials.getLogin())
				.password(hashPassword)
				.firstName(userRegDto.getFirstName())
				.lastName(userRegDto.getLastName())
				.role("User")
				.expdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()))
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
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		if (userRegDto.getFirstName() != null) {
			userAccount.setFirstName(userRegDto.getFirstName());
		}
		if (userRegDto.getLastName() != null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

//	@Override
//	public UserProfileDto removeUser(String login, String token) {
//		String level = "Admin";
//		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
//		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
//		if (userPermision(token)>=userRolesRepository.findById(level).get().getValue()||userAccount.getLogin().equals(login)) {
//			if (userAccount != null) {
//				userRepository.delete(userAccount);
//			}
//			return convertToUserProfileDto(userAccount);
//		}
//		throw new AcessForbiddenException();
//	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		String target = "removeUser";
		boolean check = checkUserPermission(target, token, login);
		if (check) {
			if (userAccount != null) {
				userRepository.delete(userRepository.findById(login).get());
			}
			return convertToUserProfileDto(userRepository.findById(login).get());
		}
		throw new AcessForbiddenException();
	}

	
//	@Override
//	public Set<String> addRole(String login, String role, String token) {
//		String level = "Admin";
//		if (userPermision(token)>=userRolesRepository.findById(level).get().getValue()) {
//			UserAccount userAccount = userRepository.findById(login).orElse(null);
//			if (userAccount != null) {
//				userAccount.addRole(role);
//				userRepository.save(userAccount);
//			} else {
//				return null;
//			}
//			return userAccount.getRoles();
//		}
//		throw new AcessForbiddenException();
//	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		String target = "addRole";
		boolean check = checkUserPermission(target, token, login);
		if (check) {
			UserAccount userAccount = userRepository.findById(login).orElse(null);
			if (userAccount != null) {
				userAccount.addRole(role);
				userRepository.save(userAccount);
			} else {
				return null;
			}
			return userAccount.getRoles();
		}
		throw new AcessForbiddenException();
	}

	
//	@Override
//	public Set<String> removeRole(String login, String role, String token) {
//		String level = "Moderator";
//		if (userPermision(token)>=userRolesRepository.findById(level).get().getValue()) {
//			UserAccount userAccount = userRepository.findById(login).orElse(null);
//			if (userAccount != null) {
//				userAccount.removeRole(role);
//				userRepository.save(userAccount);
//			} else {
//				return null;
//			}
//			return userAccount.getRoles();
//		}
//		throw new AcessForbiddenException();
//	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		String target = "removeRole";
		boolean check = checkUserPermission(target, token, login);
		if (check) {
			UserAccount userAccount = userRepository.findById(login).orElse(null);
			if (userAccount != null) {
				userAccount.removeRole(role);
				userRepository.save(userAccount);
			} else {
				return null;
			}
			return userAccount.getRoles();
		}
		throw new AcessForbiddenException();
	}

	
	@Override
	public void changePassword(String password, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		userAccount.setPassword(hashPassword);
		userAccount.setExpdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()));
		userRepository.save(userAccount);
	}

	@Override
	public UserProfileDto loginUser(String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		return convertToUserProfileDto(userAccount);
	}
	
//	private int userPermision(String token) {
//		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
//		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
//		String[] roles = userAccount.getRoles().toArray(new String[userAccount.getRoles().size()]);
//		int res = 0;
//		for (int i = 0; i < roles.length; i++) {
//			try {
//				int res1 = userRolesRepository.findById(roles[i]).get().getValue();
//				if (res1 > res) {
//					res = res1;
//				} 
//			} catch (Exception e) {
//				try {
//					throw new WrongUserRoleException();
//				} catch (WrongUserRoleException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//		return res;
//	}
	
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
