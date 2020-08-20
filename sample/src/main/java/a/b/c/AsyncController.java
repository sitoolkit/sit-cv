package a.b.c;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

  AsyncService service;

  @GetMapping
  public String get(String request) throws InterruptedException, ExecutionException {

    service.asyncWithoutResult(1);

    CompletableFuture<String> future = service.asyncWithResult(2);

    return future.get();
  }
}
