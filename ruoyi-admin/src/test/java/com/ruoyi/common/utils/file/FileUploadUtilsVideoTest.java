package com.ruoyi.common.utils.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class FileUploadUtilsVideoTest
{
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
}
