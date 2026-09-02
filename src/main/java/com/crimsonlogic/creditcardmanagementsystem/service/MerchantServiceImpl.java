package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.MerchantDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.MerchantRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl implements IMerchantService {

    private final MerchantRepository merchantRepository;
 //   private final IdGenerationUtil idGenerationUtil;

    public MerchantServiceImpl(MerchantRepository merchantRepository
                               ) {
        this.merchantRepository = merchantRepository;
        
    }

    @Override
    public MerchantDto addMerchant(MerchantDto merchantDto) {

        String merchantId;

        do {
            merchantId = IdGenerationUtil.generateMerchantId();
        } while (merchantRepository.existsById(merchantId));

        Merchant merchant = new Merchant();

        merchant.setMerchantId(merchantId);
        merchant.setMerchantName(merchantDto.getMerchantName());
        merchant.setMerchantCategory(
                merchantDto.getMerchantCategory()
        );
        merchant.setLocation(merchantDto.getLocation());
        merchant.setContactDetails(
                merchantDto.getContactDetails()
        );

        Merchant savedMerchant = merchantRepository.save(merchant);

        return convertToDto(savedMerchant);
    }

    @Override
    public MerchantDto getMerchantById(String merchantId) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found with ID: " + merchantId
                        )
                );

        return convertToDto(merchant);
    }

    private MerchantDto convertToDto(Merchant merchant) {

        MerchantDto merchantDto = new MerchantDto();

        merchantDto.setMerchantId(merchant.getMerchantId());
        merchantDto.setMerchantName(merchant.getMerchantName());
        merchantDto.setMerchantCategory(
                merchant.getMerchantCategory()
        );
        merchantDto.setLocation(merchant.getLocation());
        merchantDto.setContactDetails(
                merchant.getContactDetails()
        );

        return merchantDto;
    }
}