package com.bglore.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bglore.Candidate.Candidate;

@RestController
	public class VoteController1 {
	    private static Map<String, Candidate> candidates = new ConcurrentHashMap<>();

	    @GetMapping("/entercandidate")
	    public ResponseEntity<String> enterCandidate(@RequestParam String name) {
	       
	        if (candidates.containsKey(name)) {
	            return ResponseEntity.badRequest().body("Candidate already exists");
	        }

	        Candidate newCandidate = new Candidate();
	        newCandidate.setName(name);
	        newCandidate.setVoteCount(0);
	        candidates.put(name, newCandidate);

	        return ResponseEntity.ok("Candidate entered successfully.");
	    }

	    @PostMapping("/castvote")
	    public ResponseEntity<Object> castVote(@RequestParam String name) {
	        
	        Candidate candidate = candidates.get(name);
	        if (candidate != null) {
	            // Increment the vote count
	            candidate.incrementVoteCount();
	            int voteCount = candidate.getVoteCount();
	            return ResponseEntity.ok(voteCount);
	        } else {
	            return ResponseEntity.badRequest().body("Invalid candidate");
	        }
	    }

	    @GetMapping("/countvote")
	    public ResponseEntity<Object> countVote(@RequestParam String name) {
	        // Retrieve the candidate from the map
	        Candidate candidate = candidates.get(name);
	        if (candidate != null) {
	            int voteCount = candidate.getVoteCount();
	            return ResponseEntity.ok(voteCount);
	        }
	        else {
	            return ResponseEntity.badRequest().body("Invalid candidate");
	        }
	    }

	    @GetMapping("/listvote")
	    public ResponseEntity<Map<String, Integer>> listVotes() {
	        Map<String, Integer> candidateVoteMap = candidates.entrySet().stream()
	                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getVoteCount()));

	        return ResponseEntity.ok(candidateVoteMap);
	    }
	    @GetMapping("/getwinner")
	    public ResponseEntity<Object> getWinner() {
	        String winnerName = findWinner();

	        if (winnerName != null) {
	            return ResponseEntity.ok(Collections.singletonMap("winnerName", winnerName));
	        } 
	        else {
	            Map<String, String> errorResponses = Collections.singletonMap("error", "No winner found");
	           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponses);
	        }
			
	    

	    private String findWinner() {
	        String winnerName = null;
	        int maxVotes = Integer.MIN_VALUE;

	        for (Map.Entry<String, Candidate> entry : candidates.entrySet()) {
	            Candidate candidate = entry.getValue();
	            int candidateVotes = candidate.getVoteCount();

	            if (candidateVotes > maxVotes) {
	                maxVotes = candidateVotes;
	                winnerName = entry.getKey();
	            }
	        }

	        return winnerName;
	    }
}



