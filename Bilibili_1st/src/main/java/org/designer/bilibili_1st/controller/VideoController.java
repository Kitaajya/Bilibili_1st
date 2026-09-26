package org.designer.bilibili_1st.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.common.ResultMapper;
import org.designer.bilibili_1st.entity.VideoEntity;
import org.designer.bilibili_1st.mapper.VideoMapper;
import org.designer.bilibili_1st.service.VideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final VideoMapper videoMapper;

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam long userId,
                                              @RequestParam String title,
                                              @RequestParam("file") MultipartFile file) {
        return ResultMapper.from(videoService.uploadVideo(userId, title, file));
    }

    @GetMapping("/all")
    public Result<List<Map<String, Object>>> selectAllVideo() {
        return Result.success(videoService.selectAllVideo());
    }

    @GetMapping("/byUser")
    public Result<List<Map<String, Object>>> selectVideoByUserId(@RequestParam long userId) {
        return Result.success(videoService.selectVideoByUserId(userId));
    }

    @GetMapping("/byId")
    public Result<List<Map<String, Object>>> selectVideoById(@RequestParam long id) {
        return Result.success(videoService.selectVideoById(id));
    }

    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchVideo(@RequestParam String keyword) {
        return Result.success(videoService.searchVideo(keyword));
    }

    @PostMapping("/edit/title")
    public Result<Map<String, Object>> editTitle(@RequestParam long userId,
                                                 @RequestParam long videoId,
                                                 @RequestParam String title) {
        return ResultMapper.from(videoService.editTitle(userId, videoId, title));
    }

    @DeleteMapping("/delete")
    public Result<Map<String, Object>> deleteVideo(@RequestParam long userId,
                                                   @RequestParam long videoId) {
        return ResultMapper.from(videoService.deleteVideo(userId, videoId));
    }

    @PostMapping("/comment/write")
    public Result<Map<String, Object>> writeComment(@RequestParam long videoId,
                                                    @RequestParam long userId,
                                                    @RequestParam String contents) {
        return ResultMapper.from(videoService.writeComment(videoId, userId, contents));
    }

    @GetMapping("/comment/list")
    public Result<List<Map<String, Object>>> commentList(@RequestParam long videoId) {
        return Result.success(videoService.selectCommentsByVideoId(videoId));
    }

    @GetMapping("/play")
    public void play(@RequestParam long id,
                     @RequestHeader(value = "Range", required = false) String rangeHeader,
                     HttpServletResponse response) throws IOException {
        File file = videoService.getVideoFile(id);
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        VideoEntity entity = new VideoEntity();
        entity.setVideoPath(file.getAbsolutePath());
        String mimeType = entity.getMimeType();
        long fileLength = file.length();
        long start = 0;
        long end = fileLength - 1;
        boolean partial = false;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring("bytes=".length()).split("-", 2);
            try {
                start = parts[0].isEmpty() ? 0 : Long.parseLong(parts[0]);
                end = parts.length > 1 && !parts[1].isEmpty() ? Long.parseLong(parts[1]) : fileLength - 1;
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                response.setHeader("Content-Range", "bytes */" + fileLength);
                return;
            }
            if (start >= fileLength || start > end) {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                response.setHeader("Content-Range", "bytes */" + fileLength);
                return;
            }
            end = Math.min(end, fileLength - 1);
            partial = true;
        }
        long contentLength = end - start + 1;
        if (start == 0) videoService.recordView(id);
        response.setStatus(partial ? HttpServletResponse.SC_PARTIAL_CONTENT : HttpServletResponse.SC_OK);
        response.setContentType(mimeType);
        response.setHeader("Accept-Ranges", "bytes");
        if (partial) {
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        }
        response.setContentLengthLong(contentLength);
        response.setBufferSize(64 * 1024);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             ServletOutputStream out = response.getOutputStream()) {
            raf.seek(start);
            byte[] buffer = new byte[128 * 1024];
            long remaining = contentLength;
            while (remaining > 0) {
                int read = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read < 0) break;
                out.write(buffer, 0, read);
                remaining -= read;
            }
            out.flush();
        }
    }

    @GetMapping("/find/user")
    public Result<List<Map<String, Object>>> findUserByVirtualName(@RequestParam String virtualName) {
        return Result.success(videoService.findUserByVirtualName(virtualName));
    }

    @PostMapping("/like/toggle")
    public Result<Map<String, Object>> toggleLike(@RequestParam long videoId,
                                                  @RequestParam long userId) {
        return ResultMapper.from(videoService.toggleLike(videoId, userId));
    }

    @GetMapping("/like/status")
    public Result<Map<String, Object>> likeStatus(@RequestParam long videoId,
                                                  @RequestParam(required = false) Long userId) {
        return ResultMapper.from(videoService.getLikeStatus(videoId, userId == null ? -1 : userId));
    }
    @GetMapping("/byLikes")
    public Result<List<Map<String, Object>>> selectVideoByLikes() {
        return Result.success(videoService.selectVideoByLikes());
    }
    @GetMapping("/select/like")
    //查看谁点赞了我的视频
    public List<Map<String,Object>> selectWhoGiveMeLike(){
        return videoMapper.selectWhoGiveMeLike();
    }
    @GetMapping("/select/history")
    //查看观看历史
    public Result<List<Map<String,Object>>> selectHistoricalVideo(@RequestParam long userId){
        return Result.success(videoService.selectHistoricalVideo(userId));
    }
    @PostMapping("/record/history")
    //记录/更新观看进度，同一用户同一视频只保留一条，重复观看刷新时间和进度
    public Result<Map<String,Object>> recordHistory(@RequestParam long userId,
                                                    @RequestParam long videoId,
                                                    @RequestParam int progress) {
        return Result.success(videoService.recordHistory(userId, videoId, progress));
    }
    @DeleteMapping("/delete/single/history")
    //清空指定的历史观看视频
    public Result<Map<String,Object>> clearHistory(@RequestParam long userId,
                                                   @RequestParam long videoId){
        return Result.success(videoService.clearHistory(userId, videoId));
    }
    @DeleteMapping("/delete/all/history")
    //清空所有观看历史
    public Result<Map<String,Object>> clearAllHistory(long userId){
        return Result.success(videoService.clearAllHistory(userId));
    }
}

