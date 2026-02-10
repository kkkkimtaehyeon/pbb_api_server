package com.nhnacademy.shop.deliveryAddress.service;

import com.nhnacademy.shop.deliveryAddress.dto.DeliveryAddressRequest;
import com.nhnacademy.shop.deliveryAddress.dto.DeliveryAddressResponse;
import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import com.nhnacademy.shop.deliveryAddress.repository.DeliveryAddressRepository;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public DeliveryAddress validateDeliveryAddress(Long memberId, Long deliveryAddressId) {
        return deliveryAddressRepository.findByIdAndMember_Id(deliveryAddressId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("배송지 정보가 존재하지 않습니다."));
    }

    @Transactional
    public void createDeliveryAddress(Long memberId, DeliveryAddressRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보가 존재하지 않습니다."));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            deliveryAddressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                    .ifPresent(deliveryAddress -> {
                        deliveryAddress.updateDefault(false);
                        deliveryAddressRepository.save(deliveryAddress);
                    });
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .receiver(request.getReceiver())
                .phoneNumber(request.getPhoneNumber())
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .member(member)
                .build();

        deliveryAddressRepository.save(deliveryAddress);
    }

    @Transactional
    public DeliveryAddressResponse updateDeliveryAddress(Long memberId, Long deliveryAddressId,
                                                         DeliveryAddressRequest request) {

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdAndMember_Id(deliveryAddressId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("배송지 정보가 존재하지 않습니다."));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            deliveryAddressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                    .ifPresent(da -> {
                        if (!da.getId().equals(deliveryAddress.getId())) {
                            da.updateDefault(false);
                            deliveryAddressRepository.save(da);
                        }
                    });
            deliveryAddress.updateDefault(true);
        } else if (Boolean.FALSE.equals(request.getIsDefault())) {
            deliveryAddress.updateDefault(false);
        }

        deliveryAddress.update(
                request.getReceiver(),
                request.getPhoneNumber(),
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail());

        deliveryAddressRepository.save(deliveryAddress);

        return DeliveryAddressResponse.from(deliveryAddress);
    }

    @Transactional
    public void deleteDeliveryAddress(Long deliveryAddressId, Long memberId) {
        if (!deliveryAddressRepository.existsByIdAndMember_Id(deliveryAddressId, memberId)) {
            throw new IllegalArgumentException("배송지 정보가 존재하지 않습니다.");
        }
        deliveryAddressRepository.deleteById(deliveryAddressId);
    }

    @Transactional(readOnly = true)
    public DeliveryAddressResponse getDeliveryAddress(Long deliveryAddressId, Long memberId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByIdAndMember_Id(deliveryAddressId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("배송지 정보가 존재하지 않습니다."));
        return DeliveryAddressResponse.from(deliveryAddress);
    }

    @Transactional(readOnly = true)
    public List<DeliveryAddressResponse> getDeliveryAddresses(Long memberId) {
        return deliveryAddressRepository.findAllByMember_Id(memberId).stream()
                .map(DeliveryAddressResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryAddressResponse getDefaultDeliveryAddress(Long memberId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                .orElseThrow(() -> new IllegalArgumentException("배송지 정보가 존재하지 않습니다."));
        return DeliveryAddressResponse.from(deliveryAddress);
    }
}
