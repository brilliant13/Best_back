package bestDAOU.PicMessage_backend.controller;

import bestDAOU.PicMessage_backend.dto.FriendsDto;
import bestDAOU.PicMessage_backend.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    @PostMapping("/member/{memberId}")
    public ResponseEntity<FriendsDto> addFriend(@PathVariable Long memberId,
                                                @RequestBody FriendsDto friendsDto) {
        FriendsDto savedFriend = friendsService.addFriend(friendsDto, memberId);
        return new ResponseEntity<>(savedFriend, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<FriendsDto> getFriendById(@PathVariable("id") Long friendId) {
        FriendsDto friendDto = friendsService.getFriendById(friendId);
        return ResponseEntity.ok(friendDto);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FriendsDto>> getFriendsByMemberId(@PathVariable("memberId") Long memberId) {
        List<FriendsDto> friends = friendsService.getFriendsByMemberId(memberId);
        return ResponseEntity.ok(friends);
    }

    @PatchMapping("{id}")
    public ResponseEntity<FriendsDto> updateFriend(@PathVariable Long id,
                                                   @RequestBody FriendsDto friendsDto) {
        FriendsDto updatedFriend = friendsService.updateFriend(id, friendsDto);
        return ResponseEntity.ok(updatedFriend);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteFriend(@PathVariable("id") Long friendId) {
        friendsService.deleteFriend(friendId);
        return ResponseEntity.ok("Friend deleted successfully.");
    }

}
