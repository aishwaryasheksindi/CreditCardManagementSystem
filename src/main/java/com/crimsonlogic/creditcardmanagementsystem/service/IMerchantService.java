package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantDto;

public interface IMerchantService {

    MerchantDto addMerchant(MerchantDto merchantDto);

    MerchantDto getMerchantById(String merchantId);
}