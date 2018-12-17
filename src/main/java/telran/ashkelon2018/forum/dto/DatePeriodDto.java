package telran.ashkelon2018.forum.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class DatePeriodDto {
	@JsonFormat(pattern="yyyy-MM-dd")
	@NonNull LocalDateTime from;
	@JsonFormat(pattern="yyyy-MM-dd")
	@NonNull LocalDateTime to;

}
