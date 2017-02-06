package com.glitter.demo.redis.demo;

import org.junit.Before;
import org.junit.Test;

public class TestPiPelineDemo {
	PipelineDemo piPelineDemo = null;
	int count = 0;
	
	@Before
	public void setup(){
		piPelineDemo = new PipelineDemo();
		count = 100000;
	}
	
	@Test
    public void testWithoutPipeline(){
		piPelineDemo.withoutPipeline(count);
		piPelineDemo.usePipeline(count);
    }
	
	@Test
    public void testUserPipeline(){
		piPelineDemo.usePipeline(count);
    }
	
	@Test
    public void testDemo(){
		piPelineDemo.demo();
    }
    
}