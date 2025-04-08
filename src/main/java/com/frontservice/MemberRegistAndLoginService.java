package com.frontservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
@SessionAttributes("member") // ✅ 新增
@Service("memberService")
public class MemberRegistAndLoginService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private final String uploadDir = "upload/member";
    @ModelAttribute("member") // ✅ 新增，提供 Session 預設值
    public MemberVO prepareMember() {
        MemberVO member = new MemberVO();
        member.setOrganization(new OrganizationVO());
        return member;
    }
    public MemberVO prepareMemberForSession(MemberVO member, MultipartFile kycFile, String agreedToTerms) throws IOException {
        if (!"true".equals(agreedToTerms)) {
            throw new IllegalArgumentException("請詳閱並同意使用須知");
        }

        if (memberRepository.existsByAccount(member.getAccount())) {
            throw new IllegalArgumentException("帳號已被註冊，請選擇其他帳號");
        }

        member.setOrganization(resolveOrganization(member.getOrganization()));

        // ✅ 若使用者已有圖片（例如從確認頁回來），則保留
        String oldImage = member.getKycImage();

        if (kycFile != null && !kycFile.isEmpty()) {
            // 有新上傳就覆蓋
            String savedFilename = saveKycFile(kycFile);
            member.setKycImage(savedFilename);
        } else if (oldImage != null && !oldImage.isEmpty()) {
            // 沒上傳新檔案就保留原圖
            member.setKycImage(oldImage);
        }

        return member;
    }



    public void finalizeRegistration(MemberVO member) {
        member.setRegTime(new Timestamp(System.currentTimeMillis()));
        memberRepository.save(member);
    }

    private OrganizationVO resolveOrganization(OrganizationVO org) {
        Integer orgId = org.getOrganizationId();

        if (orgId == null) {
            throw new IllegalArgumentException("未選擇單位或單位 ID 為空");
        }

        if (orgId == -1) {
            String name = org.getName();
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("請填寫其他單位名稱");
            }
            String trimmedName = name.trim().toLowerCase();
            organizationRepository.findByName(trimmedName).ifPresent(o -> {
                throw new IllegalArgumentException("該單位名稱已存在，請從下拉選單中選擇");
            });

            OrganizationVO newOrg = new OrganizationVO();
            newOrg.setName(trimmedName);
            newOrg.setType(org.getType());
            newOrg.setStatus(0);
            newOrg.setCreatedTime(new Timestamp(System.currentTimeMillis()));

            return organizationRepository.save(newOrg);
        } else {
            return organizationRepository.findById(orgId)
                    .orElseThrow(() -> new IllegalArgumentException("無效的單位 ID"));
        }
    }

    public String saveKycFile(MultipartFile kycFile) throws IOException {
        if (kycFile == null || kycFile.isEmpty()) {
            throw new IllegalArgumentException("請上傳身分驗證檔案");
        }

        String originalFilename = kycFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        if (!Arrays.asList(".pdf", ".jpg", ".jpeg", ".png").contains(extension)) {
            throw new IllegalArgumentException("僅接受 PDF、JPG、JPEG、PNG 格式的檔案");
        }

        String savedFilename = UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(savedFilename);
        Files.copy(kycFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return savedFilename;
    }

    public String getUploadPath() {
        return uploadDir;
    }

    public void addMember(MemberVO memberVO) {
        memberRepository.save(memberVO);
    }

    public void updateMember(MemberVO memberVO) {
        memberRepository.save(memberVO);
    }

    public void deleteMember(Integer memberId) {
        if (memberRepository.existsById(memberId)) {
            memberRepository.deleteByMemberId(memberId);
        }
    }

    public MemberVO getOneMember(Integer memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public List<MemberVO> getAll() {
        return memberRepository.findAll();
    }
}