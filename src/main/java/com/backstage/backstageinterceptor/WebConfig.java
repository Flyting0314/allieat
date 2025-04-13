package com.backstage.backstageinterceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    //設定攔截的範圍，目前僅開放/backStage與/backStage/login可以不帶有Token存取。
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/backStage/**")
                .excludePathPatterns("/backStage/login",
                                     "/backStage");
    }
    
    /**
     * === 靜態資源對應設定 ===
     * 將網址開頭為 /member/ 的請求，映射到本機的 "upload/member/" 資料夾。
     *
     * 例如：
     *     網址  http://localhost:8080/member/kyc123.png
     *     對應檔案位置為 upload/member/kyc123.png
     *
     * 注意事項：
     * 1. 這個 upload/member 資料夾不會被打包進 jar。
     * 2. 所以專案部署到伺服器後，**必須自行建立 upload/member 資料夾**，
     *    並將圖片、PDF 等檔案手動複製到該資料夾。
     * 3. 若該資料夾不存在，會導致靜態資源回傳 404。
     *
     * 此作法適合「檔案會變動」或「使用者上傳」的情境。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/member/**")
                .addResourceLocations("file:upload/member/");
    
    }
}
    
 
