module aion.boot {
    requires aion.crypto;
    requires aion.apiserver;
    requires aion.zero.impl;
    requires aion.log;
    requires aion.evtmgr;
    requires slf4j.api;
    requires aion.p2p;
    requires aion.fastvm;
    requires aion.txpool;
    requires libnzmq;

    uses org.aion.evtmgr.EventMgrModule;
    uses org.aion.log.AionLoggerFactory;

    requires aion.util;
    requires aion.avm.stub;

    exports org.aion;
}
