package sample;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by dgitelson on 9/3/15.
 */
@RequestMapping("/sample")
public interface SampleServiceApi {
    @RequestMapping("/somePoint")
    List<Result> sample();

    @RequestMapping(value = "/pointAsList/{x}/{y}", method = RequestMethod.POST)
    Result[] singlePointAsArray(@PathVariable("x") int x, @PathVariable("y") int y);

    @RequestMapping(value = "/transpose", method = RequestMethod.POST)
    Result transpose(@RequestBody Input point);


    class Input {
        public int x, y;

        public Input() {
        }

        public Input(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Input{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    class Result {
        public int x, y;

        public Result() {
        }

        public Result(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}

