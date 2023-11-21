package com.project.sbarchive.controller.board;

import com.project.sbarchive.dto.board.BoardAllDTO;
import com.project.sbarchive.dto.board.BoardDTO;
import com.project.sbarchive.dto.board.BoardReportDTO;
import com.project.sbarchive.dto.page.PageRequestDTO;
import com.project.sbarchive.dto.page.PageResponseDTO;
import com.project.sbarchive.service.board.BoardFileService;
import com.project.sbarchive.service.board.BoardReprotService;
import com.project.sbarchive.service.board.BoardService;
import com.project.sbarchive.service.user.UserService;
import com.project.sbarchive.vo.board.BoardReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/boardReport")
public class BoardReportController {
    private final BoardReprotService boardReportService;

    private final BoardService boardService;    ;

    private final BoardFileService boardFileService;

    private final UserService userService;

    private final ModelMapper modelMapper;


    @GetMapping("/add")
    public void addBoard(int boardId, Principal principal, Model model) {
        BoardDTO boardDTO = boardService.getBoard(boardId);
        BoardAllDTO boardAllDTO = modelMapper.map(boardDTO,BoardAllDTO.class);
        model.addAttribute("dto", boardAllDTO);
        model.addAttribute("user",principal);
    }

    @PostMapping("/add")
    public String addBoard(BoardReportDTO boardDTO, List<MultipartFile> files,Principal principal,
                           RedirectAttributes redirectAttributes) {
        log.info("addBoard -------" +  boardDTO);
        boardDTO.setUserId(principal.getName());
        int boardId = boardReportService.add(boardDTO);
        for(MultipartFile file : files) {
            log.info(file);
        }
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                log.info("File: " + file.getOriginalFilename());
            }
            boardFileService.addBoardImages(boardId, files,"report");
        }
        return "redirect:/board/list";
    }

    @GetMapping("/list")
    public void list(Model model, @Valid PageRequestDTO pageRequestDTO,
                     BindingResult bindingResult) {
        log.info(pageRequestDTO);
        if(bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }
        PageResponseDTO<BoardReportDTO> boardDTOPageResponseDTO = boardReportService.getList(pageRequestDTO);
        List<BoardReportDTO> dtoList = boardDTOPageResponseDTO.getDtoList();
        for(BoardReportDTO dto : dtoList) {
            log.info(dto+"DTOLIST!!!!!!!!!!!!!");
        }
        model.addAttribute("responseDTO",boardDTOPageResponseDTO );
    }
    @GetMapping("/read")
    public void view(Model model, int rBoardId,  Principal principal,
                     List<MultipartFile> files, PageRequestDTO pageRequestDTO) {
        BoardReportDTO boardReportDTO = boardReportService.getBoard(rBoardId);
        log.info(boardReportDTO.getAddDate() + "------------------");
        BoardAllDTO boardAllDTO = modelMapper.map(boardReportDTO, BoardAllDTO.class);
        log.info(boardAllDTO.getAddDate());
        boardAllDTO.setAddDate(boardReportDTO.getAddDate().atStartOfDay());
        boardAllDTO.setFiles(boardFileService.getBoardImages(boardReportDTO.getRBoardId() , "report"));
        model.addAttribute("dto", boardAllDTO);
        log.info("CONTROLLER VIEW!!" + boardAllDTO);
    }
}
