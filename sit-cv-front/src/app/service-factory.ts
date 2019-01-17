import { DesignDocService } from "./srv/designdoc/designdoc.service";
import { ReportDataLoader } from "./srv/shared/report-data-loader";
import { SitCvWebsocket } from "./srv/shared/sit-cv-websocket";
import { Config } from "./srv/shared/config";
import { DesignDocReportService } from "./srv/designdoc/designdoc-report.service";
import { DesignDocServerService } from "./srv/designdoc/designdoc-server.service";
import { FunctionModelService } from "./srv/function-model/function-model.service";
import { FunctionModelReportService } from "./srv/function-model/function-model-report.service";
import { FunctionModelServerService } from "./srv/function-model/function-model-server.service";

export class ServiceFactory {

  public createDesignDocService(
    reportLoader: ReportDataLoader,
    socket: SitCvWebsocket,
    config: Config
  ): DesignDocService {
    if (config.isReportMode()) {
      return new DesignDocReportService(reportLoader);
    } else {
      return new DesignDocServerService(socket);
    }
  }

  public createFunctionModelService(
    reportLoader: ReportDataLoader,
    socket: SitCvWebsocket,
    config: Config
  ): FunctionModelService {
    if (config.isReportMode()) {
      return new FunctionModelReportService(reportLoader);
    } else {
      return new FunctionModelServerService(socket);
    }
  }

}