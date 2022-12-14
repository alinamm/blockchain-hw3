import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.websocket.WebSocketService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

    public static void main(String[] args) throws IOException {
        Configuration config = new Configuration();
        WebSocketService service = new WebSocketService(config.nodeUrl, false);
        Web3j web3j = Web3j.build(service);
        service.connect();
        web3j.blockFlowable(false).subscribe(
                block -> System.out.printf("new block: ts = " + block.getBlock().getTimestamp() + ", number = "
                        + block.getBlock().getNumber() + ", hash = " +  block.getBlock().getHash() + "%n")
        );

        config.oracles.forEach((name, address) ->  web3j.logsNotifications(List.of(address),
                                List.of(EventEncoder.encode(ExchangeRate.ANSWERUPDATED_EVENT))).subscribe(
                                l -> {
                                    EventValues values = new EventValues(
                                            IntStream.range(0, ExchangeRate.ANSWERUPDATED_EVENT.getIndexedParameters().size())
                                                    .mapToObj(i -> FunctionReturnDecoder.decodeIndexedValue(
                                                            l.getParams().getResult().getTopics().get(i + 1), ExchangeRate.ANSWERUPDATED_EVENT.getIndexedParameters().get(i)
                                                    )).collect(Collectors.toList()),
                                            FunctionReturnDecoder.decode(
                                                    l.getParams().getResult().getData(),
                                                    ExchangeRate.ANSWERUPDATED_EVENT.getNonIndexedParameters()
                                            )
                                    );
                                    BigInteger current = (BigInteger) values.getIndexedValues().get(0).getValue();
                                    BigInteger roundId = (BigInteger) values.getIndexedValues().get(1).getValue();
                                    BigInteger updatedAt = (BigInteger) values.getNonIndexedValues().get(0).getValue();
                                    System.out.printf(name + " update: ts = " + updatedAt + ", current = " + current
                                            + ", roundId = " + roundId + "%n");
                                }));
    }
}
