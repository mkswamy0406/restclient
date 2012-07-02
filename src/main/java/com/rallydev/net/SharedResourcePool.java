package com.rallydev.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SharedResourcePool<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedResourcePool.class); 

    private final UsageStrategy resourceUsage;
    private final CopyOnWriteArrayList<SharedResource<T>> resources;
    private final int numberOfResources;

    public SharedResourcePool(UsageStrategy resourceUsage, SharedResource<T>... resources) {
        this(resourceUsage, asList(resources));
    }
    
    public SharedResourcePool(UsageStrategy resourceUsage, List<SharedResource<T>> resources) {
        this.resourceUsage = resourceUsage;

        this.resources = new CopyOnWriteArrayList<SharedResource<T>>(resources);
        numberOfResources = resources.size();        
    }

    public static <T> SharedResourcePool<T> of(T... resources) {
        SystemTicker ticker = new SystemTicker();
        List<SharedResource<T>> sharedResources = new ArrayList<SharedResource<T>>();
        
        for(T resource : resources) {
            sharedResources.add(new BackoffSharedResource<T>(ticker, resource));
        }

        return new SharedResourcePool<T>(new RandomUsageStrategy(), sharedResources);
    }

    public <O> O use(Transaction<T, O> transaction) {
        int startIndex = resourceUsage.next(numberOfResources);
        int lastIndex = startIndex + numberOfResources;

        for(int i = startIndex; i<lastIndex; i++) {
            int index = i % numberOfResources;
            SharedResource<T> shared = resources.get(index);

            if(shared.isAvailable()) {
                try {
                    return shared.execute(transaction);
                } catch(Exception e) {
                    LOGGER.warn(format("Transaction failed: %s", shared), e);
                }
            }
        }

        throw new NoResourcesCanBeUsedException();
    }

}
