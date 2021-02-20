package com.edi.rps;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@RestController
public class RpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpsApplication.class, args);
	}

	@Autowired
	Info info;

	@GetMapping(path = "/play/{choice}")
	public RoundResponse play(@PathVariable int choice){
		Choice userChoice = Choice.values()[choice];
		int randomNum = ThreadLocalRandom.current().nextInt(0, 2 + 1);
		Choice computerChoice = Choice.values()[randomNum];
		boolean userWin = userWins(userChoice, computerChoice);
		boolean draw = userChoice == computerChoice;
		RoundResponse round = new RoundResponse(userChoice, computerChoice, userWin, draw);
		info.getTotalRounds().add(round);
		info.getTotalRoundsSession().add(round);
		return round;
	}

	@GetMapping(path = "/rounds")
	public List<RoundResponse> rounds(){
		return info.getTotalRounds();
	}


	@GetMapping(path = "/rounds/restart")
	public void restart(){
		info.setTotalRounds(new ArrayList<>());
	}

	@GetMapping(path = "/total")
	public RoundsTable total(){
		int userWins = ((Long)info.getTotalRoundsSession().stream().filter(roundResponse -> roundResponse.isUserWin()).count()).intValue();
		int draws = ((Long)info.getTotalRoundsSession().stream().filter(roundResponse -> roundResponse.isDraw()).count()).intValue();
		int losses = ((Long)info.getTotalRoundsSession().stream().filter(roundResponse -> !roundResponse.isUserWin() && !roundResponse.isDraw()).count()).intValue();
		return new RoundsTable(userWins, draws, losses, info.getTotalRoundsSession().size());
	}



	private static boolean userWins(Choice userChoice, Choice computerChoice){
		switch (userChoice){
			case ROCK: {
				if (computerChoice == Choice.SCISSORS)
					return true;
				break;
			}
			case PAPER: {
				if (computerChoice == Choice.ROCK)
					return true;
				break;
			}
			case SCISSORS:{
				if (computerChoice == Choice.PAPER)
					return true;
				break;
			}
		}
		return false;
	}
}

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
class RoundsTable{
	private int wins;
	private int draws;
	private int losses;
	private int totalRounds;
}

@Component
@SessionScope
@Getter
@Setter
class Info {
	private List<RoundResponse> totalRounds = new ArrayList<>();
	private List<RoundResponse> totalRoundsSession = new ArrayList<>();
}



@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
class RoundResponse{
	private Choice userChoice;
	private Choice computerChoice;
	private boolean userWin;
	private boolean draw;
}

enum Choice{
	ROCK, PAPER, SCISSORS
}