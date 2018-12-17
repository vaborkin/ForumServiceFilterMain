package telran.ashkelon2018.forum.dto;

import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto {
	String id;
	@NonNull String title;
	@NonNull String content;
	@NonNull Set<String> tags;

}
