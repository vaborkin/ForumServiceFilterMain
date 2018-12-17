package telran.ashkelon2018.forum.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPostDto {
	@NonNull @Setter String title;
	@NonNull @Setter String content;
	String author;
	@NonNull @Setter Set<String> tags;


}
