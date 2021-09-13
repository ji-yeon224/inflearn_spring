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

-------------

:calendar: 21.08.30 스프링 빈과 의존관계

:heavy_check_mark: 의존성 주입(Dependency Ingection), DI : 객체 의존관계를 외부에서 넣어주는 것
생성자에 @Autowired가 있으면 스프링이 연관된 객체를 컨테이너에서 찾아서 넣어줌.

```
@Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
```

### 컴포넌트 스캔과 자동 의존관계 설정
:heavy_check_mark: @Controller, @Service, @Repository는 @Component를 포함하기 때문에 스프링 빈으로 등록이 된다. 
:heavy_check_mark: 생성자에 @Autowired를 사용하면 객체가 생성되는 시점에 스프링 컨테이너에서 해당 스프링 빈을 찾아서 주입한다. 

※ 스프링은 스프링 컨테이너에 빈을 등록할 때 싱글톤으로 등록한다. 하나만 등록해서 이를 공유한다. -> 메모리 절약 가능

### 자바 코드로 직접 스프링 빈 등록하기

```
@Configuration
public class SpringConfig {

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

}

```
새로운 파일을 생성하여 @Configuration으로 설정파일로 등록한다.
@Bean을 보고 스프링 빈에 등록해줌.
나중에 구현체를 변경을 해야하는 경우에 자바 코드로 스프링 빈을 설정하는 방식이 용이함.
 
 
### DI 방식
 
:heavy_check_mark:생성자 주입
=> 권장하는 방식
처음 생성시 주입하고 그 이후에 변경되지 않도록 함. 의존관계가 실행 중에 동적으로 바뀌는 경우가 거의 없음.

```
 @Autowired
 public MemberService(MemberRepository memberRepository) { 
        this.memberRepository = memberRepository;
    }
```

:heavy_check_mark:필드 주입

``` 
@Autowired private MemberService memberService;
```

:heavy_check_mark: setter주입
setter를 호출하려면 public으로 설정해야하는데, 잘못 바꿔질 우려 있음.

```
@Autowired
public void setMemberService(MemberService memberService){
        this.memberService = memberService;
    }
```

-----------

:calendar: 21.09.01 스프링 DB 접근기술(1)

:heavy_check_mark: MemoryMemberRepository를 실제 db연결해서 JdbcMemberRepository를 새로 만듦.

:heavy_check_mark: 기존 코드를 손대지 않고, SpringConfig파일의 설정만 수정하여 repository를 번경함. -> 스프링의 DI를 사용
```
public MemberRepository memberRepository(){
        //return new MemoryMemberRepository();
        return new JdbcMemberRepository(dataSource);
    }
```

=> 개방-폐쇄 원칙(OCP, Open-Closed Principle): 확장에는 열려있고, 수정과 변경에는 닫혀있다. 

:heavy_check_mark: 데이터를 DB에 저장하므로 스프링 서버를 재실행해도 기존 데이터가 저장되어 있다.


------------


:calendar: 21.09.07 스프링 DB 접근기술(2)

### JPA

:heavy_check_mark: JPA는 기본적인 SQL문을 직접 만들어 준다.-> 생산성 향상

:heavy_check_mark: JPA를 통한 데이터 변경은 트랜잭션 (@Transactional)안에서 실행해야 한다. 


### 스프링 데이터 JPA

:heavy_check_mark: 리포지토리에 구현클래스 없이 인터페이스 만으로 개발 완료할 수 있다.

:heavy_check_mark: 인터페이스를 통해 기본적인 CRUD를 제공한다. 

:heavy_check_mark: findByName(), findByEmail()처럼 메서드 이름으로만 조회 가능.


-------------

:calendar: 21.09.13 AOP

### AOP

:heavy_check_mark: AOP: Aspect Oriented Programming

:heavy_check_mark: 공통 관심사항과 핵심 관심사항을 분리하여 원하는 곳에만 공통 관심 사항을 적용시킨다. 

```
@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* hello.hellospring..*(..))") //원하는 적용 대상 지정
    public Object execut(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        System.out.println("START: "+joinPoint.toString());
        try{
            return joinPoint.proceed();
        }finally{
            long finish = System.currentTimeMillis();
            long timeMs = finish = start;
            System.out.println("END: "+joinPoint.toString()+" " + timeMs + "ms");

        }

    }
}
```
:heavy_check_mark: @Aspect어노테이션을 이용하여 AOP구현한다.

:heavy_check_mark: @Around 어노테이션을 이용하여 AOP를 적용할 대상을 지정한다.


### AOP 동작 방식

:heavy_check_mark: AOP적용 시 실제 클래스가 빈으로 등록되는 것이 아니라 가짜 클래스 즉, 프록시를 주입하여 AOP를 적용한다. 


