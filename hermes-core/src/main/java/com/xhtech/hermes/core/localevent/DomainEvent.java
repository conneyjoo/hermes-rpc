/**
 * Created by louis on 2020/9/18.
 */
package com.xhtech.hermes.core.localevent;


/**
 * com.xhtech.hermes.core.localevent.DomainEvent
 *
 * @author: louis
 * @time: 2020/9/18 9:27 AM
 */
public interface DomainEvent {

    /**
     * 获取发生的时间
     * @return
     */
    long getOccurredTime();

}
