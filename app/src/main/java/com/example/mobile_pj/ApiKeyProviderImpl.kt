package com.example.mobile_pj

// API 키를 안전하게 가져오는 로직을 구현합니다.
// 예: 환경 변수, Android Keystore System 등을 사용할 수 있습니다.
class ApiKeyProviderImpl : ApiKeyProvider {
    override fun getApiKey(): String {
        // API 키를 가져오는 로직을 여기에 구현합니다.
        // TODO: API 키를 안전하게 가져오는 방법으로 변경해야 합니다.
        return "AIzaSyBsVPEvgMVA_FTTTHD9BA2uD8MW0s_c9vo"
    }
}