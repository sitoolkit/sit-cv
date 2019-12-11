package a.b.c;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

  @Async
  public void asyncWithoutResult(int id) {}

  @Async
  public CompletableFuture<String> asyncWithResult(int id) {
    return CompletableFuture.completedFuture("");
  }
}
