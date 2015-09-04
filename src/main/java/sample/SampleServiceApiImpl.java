package sample;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class SampleServiceApiImpl implements SampleServiceApi {

    @Override
    @ResponseBody
    public List<Result> sample() {
        return Arrays.asList(new Result(1, 2), new Result(2, 3));
    }

    @Override
    @ResponseBody
    public Result[] singlePointAsArray(@PathVariable("x") int x, @PathVariable("y") int y) {
        return new Result[]{new Result(x, y)};
    }

    @Override
    @RequestMapping(value = "/transpose", method = RequestMethod.POST)
    @ResponseBody
    public Result transpose(@RequestBody Input point) {
        return new Result(point.y, -point.x);
    }
}
