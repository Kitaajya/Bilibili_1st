package org.designer.bilibili_1st.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.entity.VideoEntity;
import org.designer.bilibili_1st.service.VideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    //上传视频
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam long userId,
                                      @RequestParam String title,
                                      @RequestParam("file") MultipartFile file) {
        return videoService.uploadVideo(userId, title, file);
    }

    //显示全部视频
    @GetMapping("/all")
    public List<Map<String, Object>> selectAllVideo() {
        return videoService.selectAllVideo();
    }

    //查看某个人的视频
    @GetMapping("/byUser")
    public List<Map<String, Object>> selectVideoByUserId(@RequestParam long userId) {
        return videoService.selectVideoByUserId(userId);
    }

    //按id查询视频
    @GetMapping("/byId")
    public List<Map<String, Object>> selectVideoById(@RequestParam long id) {
        return videoService.selectVideoById(id);
    }

    //搜索视频
    @GetMapping("/search")
    public List<Map<String, Object>> searchVideo(@RequestParam String keyword) {
        return videoService.searchVideo(keyword);
    }

    //修改标题
    @PostMapping("/edit/title")
    public Map<String, Object> editTitle(@RequestParam long userId,
                                         @RequestParam long videoId,
                                         @RequestParam String title) {
        return videoService.editTitle(userId, videoId, title);
    }

    //删除视频
    @DeleteMapping("/delete")
    public Map<String, Object> deleteVideo(@RequestParam long userId,
                                           @RequestParam long videoId) {
        return videoService.deleteVideo(userId, videoId);
    }

    //写视频评论
    @PostMapping("/comment/write")
    public Map<String, Object> writeComment(@RequestParam long videoId,
                                            @RequestParam long userId,
                                            @RequestParam String contents) {
        return videoService.writeComment(videoId, userId, contents);
    }

    //查看某个视频的评论
    @GetMapping("/comment/list")
    public List<Map<String, Object>> commentList(@RequestParam long videoId) {
        return videoService.selectCommentsByVideoId(videoId);
    }

    //播放视频。支持 Range 请求头，可以拖动进度条/快进快退
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

        //默认返回整个文件
        long start = 0;
        long end = fileLength - 1;
        boolean partial = false;

        //解析 Range: bytes=start-end
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
        //视频初始播放（bytes 从 0 开始）记录一次播放量
        if (start == 0) videoService.recordView(id);
        response.setStatus(partial ? HttpServletResponse.SC_PARTIAL_CONTENT : HttpServletResponse.SC_OK);
        response.setContentType(mimeType);
        response.setHeader("Accept-Ranges", "bytes");
        if (partial) {
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        }
        response.setContentLengthLong(contentLength);
        response.setBufferSize(64 * 1024);

        //手动把文件指定字节区间写入响应流
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
    //搜索用户（模糊匹配昵称）
    @GetMapping("/find/user")
    public List<Map<String,Object>> findUserByVirtualName(@RequestParam String virtualName){
        return videoService.findUserByVirtualName(virtualName);
    }

    //点赞/取消点赞
    @PostMapping("/like/toggle")
    public Map<String, Object> toggleLike(@RequestParam long videoId,
                                          @RequestParam long userId) {
        return videoService.toggleLike(videoId, userId);
    }

    //查询点赞状态和获赞数
    @GetMapping("/like/status")
    public Map<String, Object> likeStatus(@RequestParam long videoId,
                                          @RequestParam(required = false) Long userId) {
        return videoService.getLikeStatus(videoId, userId == null ? -1 : userId);
    }
}