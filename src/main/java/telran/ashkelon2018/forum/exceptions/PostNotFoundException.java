package telran.ashkelon2018.forum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class PostNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1l;
	
	public PostNotFoundException(String message) {
		super(message);
	}

}
