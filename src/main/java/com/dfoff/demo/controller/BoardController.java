package com.dfoff.demo.controller;

import com.dfoff.demo.annotation.Auth;
import com.dfoff.demo.annotation.BindingErrorCheck;
import com.dfoff.demo.annotation.BoardCheck;
import com.dfoff.demo.domain.*;
import com.dfoff.demo.domain.enums.BoardType;
import com.dfoff.demo.domain.enums.useraccount.NotificationType;
import com.dfoff.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static com.dfoff.demo.controller.UserAccountController.getCharacterResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    private final SaveFileService saveFileService;

    private final CharacterService characterService;

    private final NotificationService notificationService;

    private final RedisService redisService;


    @GetMapping("/boards/")
    public ModelAndView getBoardList(@RequestParam(required = false) BoardType boardType,
                                     @RequestParam(required = false) String keyword,
                                     @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                     @RequestParam(required = false) String searchType) {
        ModelAndView mav = new ModelAndView("/board/boardList");
        mav.addObject("articles", boardService.getBoardsByKeyword(boardType, keyword, searchType, pageable));
        mav.addObject("bestArticles", boardService.getBestBoardByBoardType(boardType));
        if (boardType != null) {
            mav.addObject("boardType", boardType.toString());
        }
        mav.addObject("keyword", keyword);
        mav.addObject("searchType", searchType);
        return mav;
    }

    @GetMapping("/boards/latest")
    public ResponseEntity<?> getLatestBoardList(@RequestParam BoardType boardType,
                                     @RequestParam(required = false) String keyword,
                                     @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                     @RequestParam(required = false) String searchType) {
        return new ResponseEntity<>(boardService.getBoardsByKeyword(boardType, keyword, searchType, pageable), HttpStatus.OK);
    }


    @PostMapping("/boards/like-board")
    public ResponseEntity<?> likeBoardById(@RequestParam Long boardId, HttpServletRequest req, @AuthenticationPrincipal UserAccount.PrincipalDto principal) {
        if (redisService.checkBoardLikeLog(req.getRemoteAddr(), boardId)) {
            redisService.deleteBoardLikeLog(req.getRemoteAddr(), boardId);
            return new ResponseEntity<>(boardService.decreaseBoardLikeCount(boardId).getBoardLikeCount(), HttpStatus.OK);
        } else {
            redisService.saveBoardLikeLog(req.getRemoteAddr(), boardId);
            Board.BoardDto dto = boardService.increaseBoardLikeCount(boardId);
            if (principal != null && !principal.getUsername().equals(dto.getUserAccount().userId())) {
                notificationService.saveBoardNotification(dto.getUserAccount(), dto, "", NotificationType.BOARD_LIKE);
            }
            return new ResponseEntity<>(dto.getBoardLikeCount(), HttpStatus.OK);
        }
    }


    @GetMapping("/boards/{boardId}")
    public ModelAndView getBoardDetail(@PathVariable Long boardId,
                                       HttpServletRequest req) {
        ModelAndView mav = new ModelAndView("/board/boardDetails");
        if (!redisService.checkBoardViewLog(req.getRemoteAddr(), boardId)) {
            redisService.saveBoardViewLog(req.getRemoteAddr(), boardId);
            boardService.increaseBoardViewCount(boardId);
        }
        Board.BoardDetailResponse boardResponse = boardService.getBoardDetailById(boardId);
        mav.addObject("article", boardResponse);
        mav.addObject("boardType", boardResponse.getBoardType().toString());
        mav.addObject("bestArticles", boardService.getBestBoardByBoardType(null));
        mav.addObject("likeLog", redisService.checkBoardLikeLog(req.getRemoteAddr(), boardId));
        return mav;
    }


    @Auth
    @GetMapping("/boards/insert")
    @BoardCheck
    public ModelAndView getBoardInsert(@AuthenticationPrincipal UserAccount.PrincipalDto principal,@RequestParam String request,
                                       @RequestParam(required = false) Long id) {
        ModelAndView mav = new ModelAndView("/board/boardInsert");
        if (request.equals("add")) {
            mav.addObject("requestType", request);
        } else if (request.equals("update")) {
            Board.BoardDetailResponse boardResponse = boardService.getBoardDetailById(id);
            mav.addObject("boardResponse", boardResponse);
            mav.addObject("requestType", request);
            StringBuilder sb = new StringBuilder();
            for (String hashtag : boardResponse.getHashtags()) {
                sb.append(",").append(hashtag);
            }
            mav.addObject("hashtag", sb);
            mav.addObject("characterExist", boardResponse.getCharacter() != null);
            if (boardResponse.getCharacter() != null) {
                mav.addObject("characterName", boardResponse.getCharacter().getCharacterName());
                mav.addObject("characterId", boardResponse.getCharacter().getCharacterId());
                mav.addObject("serverId", boardResponse.getCharacter().getServerId());
            }
        }
        return mav;
    }
    @Auth
    @GetMapping("/hashtags/")
    public List<String> getHashtags(@AuthenticationPrincipal UserAccount.PrincipalDto principal, @RequestParam String query) {
        return boardService.findHashtags(query);
    }
    @Auth
    @BoardCheck
    @DeleteMapping("/boards")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal UserAccount.PrincipalDto principal,
                                         @RequestParam Long boardId) {
        boardService.getBoardSaveFile(boardId).forEach(saveFile -> saveFileService.deleteFile(saveFile.id()));
        boardService.getBoardCommentsByBoardId(boardId).stream().filter(o -> !o.getUserId().equals(principal.getUsername())).forEach(o -> {
            notificationService.saveBoardCommentNotification(o.getUserAccountDto(), o, principal.getNickname(), NotificationType.DELETE_COMMENT);
        });
        boardService.deleteBoardById(boardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Auth
    @GetMapping("/boards/characters/")
    public ResponseEntity<?> searchCharacterForBoard(@AuthenticationPrincipal UserAccount.PrincipalDto principal,@RequestParam String serverId,
                                                     @RequestParam String characterName,
                                                     @PageableDefault(size = 15) org.springframework.data.domain.Pageable pageable
                                                    ) throws InterruptedException {
        if (serverId.equals("adventure")) {
            return new ResponseEntity<>(characterService.getCharacterByAdventureName(characterName, pageable), HttpStatus.OK);
        }
        List<CharacterEntity.CharacterEntityDto> dtos = new ArrayList<>();
        List<CharacterEntity.CharacterEntityDto> dtos1 = characterService.getCharacterDtos(serverId, characterName).join();
        return getCharacterResponse(dtos, dtos1, characterService);
    }

    @Auth
    @BindingErrorCheck
    @PostMapping("/boards")
    public ResponseEntity<?> saveBoard(@AuthenticationPrincipal UserAccount.PrincipalDto principal, @RequestBody @Valid Board.BoardRequest boardRequest, BindingResult bindingResult) throws InterruptedException, IllegalAccessException {
        Set<SaveFile.SaveFileDto> set = saveFileService.getFileDtosFromRequestFileIds(boardRequest);
        if (boardRequest.serverId().equals("")) {
            return new ResponseEntity<>(boardService.createBoard(boardRequest, set, UserAccount.UserAccountDto.from(principal), null), HttpStatus.OK);
        }
        CharacterEntity.CharacterEntityDto character = characterService.getCharacter(boardRequest.serverId(), boardRequest.characterId());
        return new ResponseEntity<>(boardService.createBoard(boardRequest, set, UserAccount.UserAccountDto.from(principal), character), HttpStatus.OK);
    }

    @Auth
    @BoardCheck
    @BindingErrorCheck
    @PutMapping("/boards")
    public ResponseEntity<?> updateBoard(@AuthenticationPrincipal UserAccount.PrincipalDto principal, @RequestBody @Valid Board.BoardRequest boardRequest, BindingResult bindingResult) throws InterruptedException {
        Set<SaveFile.SaveFileDto> set = saveFileService.getFileDtosFromRequestFileIds(boardRequest);
        if(boardRequest.serverId().equals("")) {
            return new ResponseEntity<>(boardService.updateBoard(boardRequest.id(), boardRequest, set, null), HttpStatus.OK);
        }
        CharacterEntity.CharacterEntityDto character = characterService.getCharacter(boardRequest.serverId(), boardRequest.characterId());
        return new ResponseEntity<>(boardService.updateBoard(boardRequest.id(), boardRequest, set, character), HttpStatus.OK);
    }
}
