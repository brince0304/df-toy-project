package com.dfoff.demo.Service;

import com.dfoff.demo.Domain.*;
import com.dfoff.demo.Domain.EnumType.BoardType;
import com.dfoff.demo.Repository.BoardHashtagMapperRepository;
import com.dfoff.demo.Repository.BoardRepository;
import com.dfoff.demo.Repository.HashtagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final HashtagRepository hashtagRepository;

    private final BoardHashtagMapperRepository mapper;

    public Board.BoardDto createBoard(Board.BoardRequest request, Set<SaveFile.SaveFileDTO> saveFile, UserAccount.UserAccountDto dto, CharacterEntity.CharacterEntityDto character) {
        Board board_ = boardRepository.save(request.toEntity(dto.toEntity()));
        saveFile.stream().map(SaveFile.SaveFileDTO::toEntity).forEach(o-> board_.getBoardFiles().add(o));
        if(createHashtag(request.getHashtag()).size()>5){
            throw new IllegalArgumentException("해시태그는 5개까지만 등록 가능합니다.");
        }
        saveHashtagAndBoard(board_,createHashtag(request.getHashtag()));
        if(character!=null){
            board_.setCharacter(CharacterEntity.CharacterEntityDto.toEntity(character));
        }
        return Board.BoardDto.from(board_);
    }

    public List<String> createHashtag(String hashtag){
        return Arrays.stream(hashtag.replaceAll(" ","").split("#")).filter(o->!o.equals("")).collect(Collectors.toList());
    }



    public void saveHashtagAndBoard(Board board , List<String> hashtags){
        for (String hashtag : hashtags) {
            if(hashtag.length()>7) {
                throw new IllegalArgumentException("해시태그는 7자 이하로 등록 가능합니다.");
            }
            Hashtag hashtag_ = hashtagRepository.findById(hashtag).orElseGet(()-> hashtagRepository.save(Hashtag.builder().name(hashtag).build()));
            mapper.save(BoardHashtagMapper.of(board,hashtag_));
        }
    }

    public void updateHashtagAndBoard(Board board , List<String> hashtags){
        board.getHashtags().forEach(o->{
            o.setBoard(null);
            o.setHashtag(null);
        });

        for (String hashtag : hashtags) {
            Hashtag hashtag_ = hashtagRepository.findById(hashtag).orElseGet(()-> hashtagRepository.save(Hashtag.builder().name(hashtag).build()));
            mapper.save(BoardHashtagMapper.of(board,hashtag_));
        }
    }
    public Board.BoardDetailResponse getBoardDetail(Long id){
        Board board_ = boardRepository.findBoardById(id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        return Board.BoardDetailResponse.from(board_);
    }

    public String getBoardAuthor(Long id){
        Board board_ = boardRepository.findBoardById(id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        return board_.getUserAccount().getUserId();
    }

    public void increaseViewCount(Long Id){
        Board board_ = boardRepository.findBoardById(Id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        board_.setBoardViewCount(board_.getBoardViewCount()+1);
    }

    public int increaseLikeCount(Long Id){
        Board board_ = boardRepository.findBoardById(Id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        board_.setBoardLikeCount(board_.getBoardLikeCount()+1);
        return board_.getBoardLikeCount();
    }

    public int decreaseLikeCount(Long Id){
        Board board_ = boardRepository.findBoardById(Id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        board_.setBoardLikeCount(board_.getBoardLikeCount()-1);
        return board_.getBoardLikeCount();
    }



    public Page<Board.BoardListResponse> getBoardsByKeyword(BoardType boardType, String keyword, String searchType, Pageable pageable) {
        if (boardType==null&&keyword == null) {
            return boardRepository.findAll(pageable).map(Board.BoardListResponse::from);
        } else if (boardType==null&&searchType.equals("title")) {
            return boardRepository.findAllByBoardTitleContainingIgnoreCase(keyword, pageable).map(Board.BoardListResponse::from);
        } else if (boardType==null&&searchType.equals("content")) {
            return boardRepository.findAllByBoardContentContainingIgnoreCase(keyword, pageable).map(Board.BoardListResponse::from);
        }else if(boardType==null&&searchType.equals("nickname")){
            return boardRepository.findAllByUserAccountContainingIgnoreCase(keyword, pageable).map(Board.BoardListResponse::from);
        }else if(boardType==null&&searchType.equals("title_content")) {
            return boardRepository.findAllByBoardTitleContainingIgnoreCaseOrBoardContentContainingIgnoreCase(keyword, pageable).map(Board.BoardListResponse::from);
        }else if(boardType!=null&&keyword==null){
            return boardRepository.findAllByBoardType(boardType, pageable).map(Board.BoardListResponse::from);
        }else if(boardType==null&& searchType.equals("hashtag")) {
            return mapper.findAllByHashtagName(keyword, pageable).map(BoardHashtagMapper::getBoard).map(Board.BoardListResponse::from);
        }else if(boardType==null&& searchType.equals("characterName")) {
            return boardRepository.findAllByCharacterName(keyword, pageable).map(Board.BoardListResponse::from);
        }
        else if(boardType != null){
            switch (searchType) {
                case "title" -> {
                    return boardRepository.findAllByTypeAndBoardTitleContainingIgnoreCase(boardType, keyword, pageable).map(Board.BoardListResponse::from);
                }
                case "content" -> {
                    return boardRepository.findAllByTypeAndBoardContentContainingIgnoreCase(boardType, keyword, pageable).map(Board.BoardListResponse::from);
                }
                case "nickname" -> {
                    return boardRepository.findAllByTypeAndUserAccountContainingIgnoreCase(boardType, keyword, pageable).map(Board.BoardListResponse::from);
                }
                case "title_content" -> {
                    return boardRepository.findAllByTypeAndBoardTitleContainingIgnoreCaseOrBoardContentContainingIgnoreCase(boardType, keyword, pageable).map(Board.BoardListResponse::from);
                }
                case "hashtag" -> {
                    return mapper.findAllByHashtagNameAndBoardType(keyword,boardType, pageable).map(BoardHashtagMapper::getBoard).map(Board.BoardListResponse::from);
                }
            }
        }
        return boardRepository.findAll(pageable).map(Board.BoardListResponse::from);
    }

    public List<Board.BoardListResponse> getBestBoard(BoardType boardType){
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(2);
        if(boardType==null){
            return boardRepository.findBoardByLikeCount(start,end).stream().map(Board.BoardListResponse::from).toList();
        }else{
            return boardRepository.findBoardByLikeCountAndBoardType(boardType,start,end).stream().map(Board.BoardListResponse::from).toList();
        }
    }

    public Board.BoardDto updateBoard(Long id, Board.BoardRequest request, Set<SaveFile.SaveFileDTO> fileDtos, CharacterEntity.CharacterEntityDto character) {
       Board board_ =  boardRepository.findBoardById(id);
       if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
       if(request != null){
           if(createHashtag(request.getHashtag()).size()>5){throw new IllegalArgumentException("해시태그는 5개까지만 등록 가능합니다.");}
           updateHashtagAndBoard(board_,createHashtag(request.getHashtag()));
           if(request.getBoardTitle()!=null){
               board_.setBoardTitle(request.getBoardTitle());
           }
              if(request.getBoardContent()!=null){
                board_.setBoardContent(request.getBoardContent());
              }
              if(character!=null){
                    board_.setCharacter(CharacterEntity.CharacterEntityDto.toEntity(character));
              }else{
                    board_.setCharacter(null);
              }
              board_.setBoardType(request.getBoardType());
       }
       fileDtos.stream().map(SaveFile.SaveFileDTO::toEntity).forEach(o-> board_.getBoardFiles().add(o));
       return Board.BoardDto.from(board_);

    }

    public void deleteBoardById(Long id){
        Board board_ = boardRepository.findBoardById(id);
        for(BoardHashtagMapper mapper_ : board_.getHashtags()){
            mapper_.setBoard(null);
            mapper_.setHashtag(null);
        }
        board_.setCharacter(null);
        boardRepository.deleteBoardById(id);
    }

    public Set<SaveFile.SaveFileDTO> getBoardSaveFile(Long id){
        Board board_ = boardRepository.findBoardById(id);
        if(board_==null){throw new EntityNotFoundException("게시글이 존재하지 않습니다.");}
        return board_.getBoardFiles().stream().map(SaveFile.SaveFileDTO::from).collect(Collectors.toSet());
    }
}
