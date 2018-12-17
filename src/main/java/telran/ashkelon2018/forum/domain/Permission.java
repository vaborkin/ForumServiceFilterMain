package telran.ashkelon2018.forum.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of= {"target"})
@Document(collection="forum_permissions")
public class Permission {
	@Id String target;
	@Singular
	Set<String> permissions;
	
	public void addPermission(String permission) {
		permissions.add(permission);
	}

	public void removePermission(String permission) {
		permissions.remove(permission);
	}

}
