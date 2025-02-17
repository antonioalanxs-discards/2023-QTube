import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorModule } from './error/error.module';
import { InModule } from './in/in.module';
import { HomeModule } from './home/home.module';
import { WatchModule } from './watch/watch.module';
import { NewModule } from './new/new.module';
import { UpdateModule } from './update/update.module';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  exports: [
    ErrorModule,
    InModule,
    HomeModule,
    WatchModule,
    NewModule,
    UpdateModule,
  ],
})
export class ModulesModule {}
