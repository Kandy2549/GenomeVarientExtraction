package com.genome;

import org.springframework.boot.SpringApplication;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class GenomicVariantExtApplication extends SpringBootServletInitializer{
	
	
	 private int maxUploadSizeInMb = 100 * 1024 * 1024; // 10 MB

	public static void main(String[] args) {
		SpringApplication.run(GenomicVariantExtApplication.class, args);
	}
	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GenomicVariantExtApplication.class);
	}
	
	 //Tomcat large file upload connection reset
    @Bean
    public ServletWebServerFactory tomcatEmbedded() {
        return  new TomcatServletWebServerFactory() {
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                //Managing File Size maually..Kandy
                //int maxSize = 50000000;
                //connector.setMaxPostSize(maxSize);
                //connector.setMaxSavePostSize(maxSize);
                if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
                	//-1 applied for unlimited upload size..Kamlesh
                    ((AbstractHttp11Protocol <?>) connector.getProtocolHandler()).setMaxSwallowSize(maxUploadSizeInMb);
                }
            }
        };

    }

}
