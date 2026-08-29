package com.ruoyi.common.utils.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import com.ruoyi.common.exception.file.InvalidExtensionException;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtilsVideoTest
{
    private static final String[] ALLOWED_VIDEO_EXTENSIONS = { "mp4", "mov", "m4v", "avi", "rmvb" };

    @Test
    public void detectsWechatMp4ByMimeWhenFilenameHasNoExtension()
    {
        assertEquals("mp4", MimeTypeUtils.getExtension("video/mp4"));
    }

    @Test
    public void detectsIphoneMovByMimeWhenFilenameHasNoExtension()
    {
        assertEquals("mov", MimeTypeUtils.getExtension("video/quicktime"));
    }

    @Test
    public void detectsMp4HeaderWhenFilenameAndMimeAreMissing() throws Exception
    {
        MultipartFile file = mockVideo(null, null,
                new byte[] { 0, 0, 0, 24, 'f', 't', 'y', 'p', 'm', 'p', '4', '2' });

        assertEquals("mp4", FileUploadUtils.getExtension(file));
        FileUploadUtils.assertAllowed(file, ALLOWED_VIDEO_EXTENSIONS);
        String storedName = FileUploadUtils.extractFilename(file);
        assertTrue(storedName.contains("/upload_"));
        assertTrue(storedName.endsWith(".mp4"));
    }

    @Test
    public void detectsQuickTimeHeaderWithBlankFilenameAndGenericMime() throws Exception
    {
        MultipartFile file = mockVideo("", "application/octet-stream",
                new byte[] { 0, 0, 0, 24, 'f', 't', 'y', 'p', 'q', 't', ' ', ' ' });

        assertEquals("mov", FileUploadUtils.getExtension(file));
        FileUploadUtils.assertAllowed(file, ALLOWED_VIDEO_EXTENSIONS);
    }

    @Test
    public void detectsAviHeaderWithExtensionlessFilenameAndGenericMime() throws Exception
    {
        MultipartFile file = mockVideo("file", "application/octet-stream",
                new byte[] { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'A', 'V', 'I', ' ' });

        assertEquals("avi", FileUploadUtils.getExtension(file));
        FileUploadUtils.assertAllowed(file, ALLOWED_VIDEO_EXTENSIONS);
    }

    @Test
    public void rejectsUnknownBinaryInsteadOfTreatingItAsVideo() throws Exception
    {
        MultipartFile file = mockVideo(null, "application/octet-stream", new byte[] { 1, 2, 3, 4 });

        assertEquals("", FileUploadUtils.getExtension(file));
        assertThrows(InvalidExtensionException.class,
                () -> FileUploadUtils.assertAllowed(file, ALLOWED_VIDEO_EXTENSIONS));
    }

    private MultipartFile mockVideo(String originalFilename, String contentType, byte[] content) throws Exception
    {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getSize()).thenReturn((long) content.length);
        when(file.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(content));
        return file;
    }
}
