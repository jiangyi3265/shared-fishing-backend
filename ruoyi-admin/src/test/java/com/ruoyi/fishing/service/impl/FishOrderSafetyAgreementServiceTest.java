package com.ruoyi.fishing.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.service.IFishOrderService;

public class FishOrderSafetyAgreementServiceTest
{
    @Test
    public void missingConsentCannotStartOrder()
    {
        FishOrderServiceImpl service = new FishOrderServiceImpl();
        ServiceException error = assertServiceException(() ->
                service.startOrder(7L, 1L, 2L, IFishOrderService.SAFETY_AGREEMENT_VERSION, false));
        assertTrue(error.getMessage().contains("安全协议"));
    }

    @Test
    public void staleAgreementVersionCannotStartOrder()
    {
        FishOrderServiceImpl service = new FishOrderServiceImpl();
        ServiceException error = assertServiceException(() ->
                service.startOrder(7L, 1L, 2L, "2026-01-01", true));
        assertTrue(error.getMessage().contains("安全协议"));
    }

    private ServiceException assertServiceException(Runnable invocation)
    {
        try
        {
            invocation.run();
            fail("Expected ServiceException");
            return null;
        }
        catch (ServiceException error)
        {
            return error;
        }
    }
}
