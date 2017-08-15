package com.dianping.shadow.context.scanner;

import com.dianping.shadow.context.scanner.AbstractScanner;
import com.dianping.shadow.context.scanner.MemberScanner;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Member;
import java.util.Set;

import static org.hamcrest.core.Is.is;

/**
 * Created by jourrey on 16/11/10.
 */
public class MemberScannerTest {

    @Test
    public void testScan() {
        Set<Member> members = new MemberScanner.MemberScannerBuilder()
                .clazz(MemberScanner.class)
                .build()
                .doScan();
        Assert.assertThat(members.size(), is(1));
        for (Member member : members) {
            Assert.assertTrue(member.getName().equals("doScan"));
        }

        members = new MemberScanner.MemberScannerBuilder()
                .clazz(MemberScanner.class)
                .filter(new AbstractScanner.ScannerFilter() {
                    @Override
                    public boolean accept(Object o) {
                        return false;
                    }
                })
                .build()
                .doScan();
        Assert.assertThat(members.size(), is(0));
    }

}
