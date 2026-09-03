package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantResponseDto;

public interface IMerchantService {

    MerchantResponseDto addMerchant(MerchantRequestDto merchantDto);

    MerchantResponseDto getMerchantById(String merchantId);
}