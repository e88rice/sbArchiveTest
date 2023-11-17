package com.project.sbarchive.service.board;

import com.project.sbarchive.dto.board.BoardDTO;
import com.project.sbarchive.dto.page.PageRequestDTO;
import com.project.sbarchive.dto.page.PageResponseDTO;
import com.project.sbarchive.mapper.board.BoardMapper;
import com.project.sbarchive.vo.board.BoardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;
    private final ModelMapper modelMapper;

    @Override
    public int getReplyCount(int boardId) { // 보드고유번호값을대임해 리플갯수반환 int
        return boardMapper.getReplyCount(boardId);
    }

    @Override
    public int add(BoardDTO boardDTO) {
        BoardVO boardVO = modelMapper.map(boardDTO, BoardVO.class);
        boardMapper.add(boardVO);
        return boardVO.getBoardId();
    }

    @Override
    public List<BoardDTO> getBoardList() {
        List<BoardVO> boardVOList = boardMapper.getBoardList();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        boardVOList.forEach(boardVO -> boardDTOList.add(modelMapper.map(boardVO,BoardDTO.class)));
        return boardDTOList;
    }

    @Override
    public BoardDTO getBoard(int boardId) {
        BoardVO boardVO = boardMapper.getBoard(boardId);
        BoardDTO boardDTO = modelMapper.map(boardVO,BoardDTO.class);

        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        BoardVO boardVO = modelMapper.map(boardDTO,BoardVO.class);
        boardMapper.modify(boardVO);
    }

    @Override
    public void hitCount(int boardId) {
        boardMapper.hitCount(boardId);
    }

    @Override
    public void remove(int boardId) {
        boardMapper.remove(boardId);
    }

    @Override
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {
        List<BoardVO> voList = boardMapper.selectList(pageRequestDTO);

        List<BoardDTO> dtoList = new ArrayList<>();
        for (BoardVO boardVO : voList) {
            dtoList.add(modelMapper.map(boardVO, BoardDTO.class));
            for(BoardDTO boardDTOLists : dtoList) {
                boardDTOLists.setReplyCount( boardMapper.getReplyCount(boardDTOLists.getBoardId()));
            }
        }

        int total = boardMapper.getCount(pageRequestDTO);

        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .total(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
        return pageResponseDTO;
    }
}
