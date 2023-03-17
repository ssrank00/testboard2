package com.testboard2.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/*
 * @Configuration : 스프링부트 환경설정 클래스임을 명시. 자동으로 빈 등록
 * 이 애노테이션이 붙게되면 @ComponentScan이 스캔할 때 이 클래스 내부에 @Bean으로 지정한 모든 빈들도 IoC 컨테이너에 등록.
 * */
@Configuration
@PropertySource("classpath:/application.properties") // -> '/' = src/main/resources 경로를 나타냄
public class DBConfiguration {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	/* 
	 * @Bean : return되는 객체를 IoC 컨테이너에 등록
	 * 특별히 지정하는 이름이 없다면 IoC 컨테이너에 해당 메서드 명으로 등록. 특정 이름 지정도 가능. 보통 메서드 명으로 등록. 중복안됨
	 */

	// HikariCP 관련 객체 생성(DB 연동, DB Connection 객체 생성)
	/* Hikari Config #1 : application.properties 파일로부터 데이터베이스 관련된 정보를 읽어와서 HikariConfig 객체를 리턴 
	 * 					  HikariConfig <-- application.properties(spring.datasource.hikari)
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.hikari")	// 해당 접두어(spring.datasource.hikari)로 시작하는 정보들을 읽어옴 
	public HikariConfig hikariConfig() {
		
		return new HikariConfig();
	}
	
	/* Hikari Config #2 : 히카리 설정 객체(HikariConfig)를 인자로 받아 HikariDataSource 객체를 리턴(생성) 
	*					  HikariDataSource <-- HikariConfig
	* 					  이 단계 에서 데이터베이스 연결이 완성
	* 					  HikariCP(Connection Pool) 연결이 완성
	* 					  DB 연결이 필요한 부분에서 dataSource를 가지고 연결
	*/
	@Bean
	public DataSource dataSource() {
		DataSource dataSource = new HikariDataSource(hikariConfig());
		System.out.println(dataSource.toString()); // DB 연결 확인을 위해 dataSource 객체를 출력 -> "HikariDataSource(HikariPool-1)"
		
		return dataSource;
	}
	
	/* MyBatis Config #1 : SqlSessionFactory <-- SqlSessionFactoryBean
	 * 					   SqlSessionFactoryBean을 사용해 SqlSessionFactory 생성
	 *					   이떄, DataSource 객체를 인자로 받아서 처리해도 되고, 아니면 setDataSource(dataSource()) 이렇게 해줘도 됨.
	 *					   
	 * SqlSessionFactoryBean의 기본적인 설정 메소드 3가지
	 * 		setDataSource			:	빌드된 DataSource를 셋팅.
	 * 		setMapperLocations		:	SQL 구문이 작성된 *Mapper.xml의 경로를 정확히 등록.
	 * 		setTypeAliasesPackage	:	인자로 사용할 Alias(DTO) 대상 클래스가 위치한 패키지 경로를 작성.
	 * ※ 주의
	 *   SqlSessionFactory에 저장할 config 설정 시 Mapper에서 사용하고자하는 DTO, VO, Entity에 대해서 setTypeAliasesPackage 지정 필요.
	 *   만약 지정해주지 않는다면 aliases 찾지 못한다는 오류가 발생할 수 있음.
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		/*
		 * ApplicationContext는 스프링 컨테이너라고 보면 됨.
		 * ApplicationContext는 애플리케이션이 스타트해서 끝나는 순간까지 애플리케이션에서 필요한 모든 자원들을 모아놓고 관리.
		 * Mapper에 대한 리소스 역시 ApplicationContext 객체에서 관리
		 */
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/*Mapper.xml"));	//classpath의 '/'는 'src/main/resources' 임
		factoryBean.setTypeAliasesPackage("com.testboard2.dto");	// dto의 package 경로
		
		return factoryBean.getObject();
	}
	
	/* MyBatis Config #2 : SqlSessionTemplate <-- SqlSessionFactory
	 *					   SqlSessionFactory를 인자로 받아 SqlSessionTemplate 객체를 생성 및 리턴
	 *					   SqL 구문의 실행과 트랜잭션 등을 관리
	 *					   MyBatis의 sqlSession 객체를 Spring+MyBatis 연동 모듈에서는 sqlSessionTemplate로 대체 
	 */
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception{
		
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}
