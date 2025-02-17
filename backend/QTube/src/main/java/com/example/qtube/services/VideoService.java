package com.example.qtube.services;

import com.example.qtube.dtos.*;
import com.example.qtube.models.Image;
import com.example.qtube.models.Video;
import com.example.qtube.repositories.VideoRepository;
import com.example.qtube.utils.FileUtils;
import com.example.qtube.utils.RestUtils;

import org.javatuples.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final ResourceService resourceService;
    private final ImageService imageService;

    public VideoService(VideoRepository videoRepository,
            ResourceService resourceService,
            ImageService imageService) {
        this.videoRepository = videoRepository;
        this.resourceService = resourceService;
        this.imageService = imageService;
    }

    public Pair<VideoDTO, URI> save(UploadVideoDTO uploadVideoDTO) throws IOException {
        MultipartFile thumbnailMultipartFile = uploadVideoDTO.getThumbnail();
        Image image = this.imageService.save(thumbnailMultipartFile);
        Pair<VideoDTO, URI> result = this.save(uploadVideoDTO, image);
        return result;
    }

    private Pair<VideoDTO, URI> save(UploadVideoDTO uploadVideoDTO, Image image) throws IOException {
        String title = uploadVideoDTO.getTitle();
        String description = uploadVideoDTO.getDescription();
        MultipartFile videoMultipartFile = uploadVideoDTO.getVideo();
        String slug = FileUtils.slug(videoMultipartFile);
        Video video = new Video(title, description, slug, image);

        this.resourceService.upload(videoMultipartFile, slug);
        this.videoRepository.save(video);

        VideoDTO videoDTO = new VideoDTO(video);
        URI location = RestUtils.location(slug);
        return Pair.with(videoDTO, location);
    }

    public Optional<DownloadVideoDTO> video(String slug) {
        Optional<Video> optionalVideo = this.videoRepository.findBySlug(slug);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            DownloadVideoDTO downloadVideoDTO = new DownloadVideoDTO(video);
            return Optional.of(downloadVideoDTO);
        }
        return Optional.empty();
    }

    public boolean existsBySlug(String slug) {
        return this.videoRepository.existsBySlug(slug);
    }

    public void delete(String slug) {
        this.resourceService.delete(slug);
        String thumbnailSlug = this.videoRepository.findThumbnailSlugByVideoSlug(slug);
        this.resourceService.delete(thumbnailSlug);
        this.videoRepository.deleteBySlug(slug);
    }

    public Collection<PreviewVideoDTO> videos() {
        Collection<Video> videos = this.videoRepository.findAll();
        Collection<PreviewVideoDTO> previewVideoDTOs = videos.stream()
                .map(video -> new PreviewVideoDTO(video))
                .collect(Collectors.toList());
        return previewVideoDTOs;
    }

    public VideoDTO update(String slug, UpdateVideoDTO updateVideoDTO) {
        String title = updateVideoDTO.getTitle();
        String description = updateVideoDTO.getDescription();
        Video video = this.videoRepository.findBySlug(slug).get();
        video.setTitle(title);
        video.setDescription(description);

        this.videoRepository.save(video);

        VideoDTO videoDTO = new VideoDTO(video);
        return videoDTO;
    }
}
