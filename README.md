# 인프런-스프링 입문(김영한)

:calendar: 21.08.29 회원관리 예제 - 백엔드 개발  

:heavy_check_mark: 회원 가입
```
public Long join(Member member){
        //같은 이름이 있는 중복 회원x
        validateDuplicateMember(member); //중복 회원 검증

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        });
    }
    
```
### 테스트 코드로 검증하기
:heavy_check_mark: MemoryMemberRepositoryTest
Assertions 메소드 사용
member값과 result값을 비교하여 save메소드가 정상 작동하는지 확인

```
@Test
    public void save(){
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();

        assertThat(member).isEqualTo(result); //Assertion.asserThat() -> Alt+Enter로 static import하여 생략

    }
```
    
:heavy_check_mark: 여러 메소드 테스트 시 데이터 중복으로 인한 오류를 제거하기위해 콜백 메소드를 호출하여 데이터를 clear해줌.
@AfterEach 어노테이션을 이용하여 자동으로 호출되게 함.
```
@AfterEach //콜백메소드
public void afterEach(){
    repository.clearStore();
}
```
    
 
