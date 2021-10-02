package nguyenbanh.com;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello")
@Slf4j
public class HelloController {

    @GetMapping("/{id}")
    public Boolean test(@PathVariable(value = "id") Long id) {
        log.info("Id = {}", id);
        return true;
    }

    @GetMapping("/commit-offset/{id}")
    public Boolean commitOffset(@PathVariable(value = "id") Long id) {
        log.info("Commit Offset - Id = {}", id);
        return true;
    }
}
